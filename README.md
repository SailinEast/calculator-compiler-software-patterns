# AST Calculator — Strategy Design Pattern

![Java](https://img.shields.io/badge/Language-Java%2021%2B-orange.svg)
![Design Pattern](https://img.shields.io/badge/Pattern-Strategy%20(GoF)-blue.svg)
![Clean Code](https://img.shields.io/badge/Clean%20Code-Ch.%2010%20Adherent-green.svg)

An arithmetic expression compiler and computation engine featuring a multi-stage compiler pipeline (**Lexical Scanner $\rightarrow$ Recursive Descent Parser $\rightarrow$ Abstract Syntax Tree**), dual arithmetic backends (**IEEE-754 64-bit float vs. Arbitrary-Precision BigDecimal**), and interchangeable runtime output formatting algorithms powered by the **Gang of Four (GoF) Strategy Pattern**.

---

## 1. Problem Statement & Motivation

In a mathematical calculator, raw numerical evaluation and result formatting are two fundamentally different responsibilities. Embedding number-formatting logic directly inside `MathEngine` implementations or selecting output formats via `if/else` and `switch` statements inside `Calculator` creates severe architectural flaws:
1. **Violation of Single Responsibility Principle (SRP):** Arithmetic backends (`DoubleMathEngine`, `BigDecimalMathEngine`) become coupled to string presentation rules rather than focusing solely on numerical computation.
2. **Rigid Conditional Branching:** Supporting multiple calculator display modes—such as **Plain Decimal (`NORM`)**, **Fixed-Point Grouped (`FIX`)**, and **Scientific Notation (`SCI`)**—via conditional flags forces `Calculator` and `CalculatorBuilder` to modify existing `switch` blocks whenever a new format is introduced.
3. **Inability to Swap Algorithms at Runtime:** Hardcoding formatting inside engines prevents clients from dynamically switching how results and step-by-step traces are displayed on an active `Calculator` instance without recreating the backend.

The **Strategy Pattern** solves this by extracting the family of formatting algorithms into a dedicated `NumberFormatStrategy` hierarchy. The `Calculator` (Context) holds a reference to `NumberFormatStrategy`, delegates formatting execution polymorphically, and allows clients to hot-swap formatting strategies at runtime with **zero conditional branching**.

---

## 2. Technical Requirements Mapping

| Assignment Requirement          | Implementation in Project                                                                          | Details                                                                                                                                                                                              |
|:--------------------------------|:---------------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Strategy Interface**          | `interface NumberFormatStrategy`                                                                   | Declares the common execution contract `String format(Number value)` shared by all formatting algorithms.                                                                                            |
| **Concrete Strategies (3+)**    | `PlainDecimalFormatStrategy`, `FixedDecimalFormatStrategy`, `ScientificFormatStrategy`             | Encapsulate distinct formatting algorithms: trailing-zero stripping plain decimal (`NORM`), locale-grouped fixed scale (`FIX`), and normalized exponential scientific notation (`SCI`).              |
| **Context**                     | `class Calculator` (and refined `TracedCalculator`)                                                | Holds a reference to `NumberFormatStrategy`, delegates formatting in `calculate()` and trace hooks, and supports dynamic runtime switching via `setFormatStrategy(NumberFormatStrategy)`.            |
| **Client**                      | `class Main` (plus `CalculatorBuilder` & `CalculatorFacade`)                                       | Configures and switches concrete strategies at runtime on the `Calculator` context—**no `if/else` or `switch` on strategy type exists inside the Context or Builder**.                               |

---

## 3. Architecture & Structural Diagram

![uml_diagram](docs/uml_diagram.png)

### Detailed Component Roles

- **Strategy Interface (`NumberFormatStrategy`):** Defines the algorithm abstraction `String format(Number value)` that decouples the `Calculator` context from concrete string-formatting implementations.
- **Concrete Strategies (`calculator.strategy`):**
  * `PlainDecimalFormatStrategy`: Converts arbitrary `Number` results to `BigDecimal`, strips insignificant trailing zeros (`stripTrailingZeros()`), and renders an exact plain decimal string (`toPlainString()`) without exponential notation.
  * `FixedDecimalFormatStrategy`: Formats numbers to a configurable number of decimal places (default `2`, or custom `n` places) with thousands grouping separators (`%,.nf`).
  * `ScientificFormatStrategy`: Normalizes numbers into standard base-10 exponential notation ($m \times 10^n$, formatted as `%E`).
- **Context (`Calculator` & `TracedCalculator`):** Maintains the active `NumberFormatStrategy` reference. `Calculator.calculate()` delegates final output rendering to `formatStrategy.format(result)`, while `TracedCalculator` delegates formatting of every intermediate AST evaluation step (`onLiteral`, `onUnary`, `onBinary`, `afterCalculation`) to the active strategy.
- **Fluent Builder & Subsystem Facade (`CalculatorBuilder` & `CalculatorFacade`):**
  * `CalculatorBuilder`: Reuses shared, immutable `static final` strategy instances (`PLAIN_FORMAT`, `SCIENTIFIC_FORMAT`, `DEFAULT_FIXED_FORMAT`) or accepts custom `NumberFormatStrategy` instances via `.format(strategy)` without conditional branching.
  * `CalculatorFacade`: Propagates runtime strategy changes across pre-configured standard and traced calculators via `setFormatStrategy(NumberFormatStrategy)`.

---

## 4. Five Clean Code Principles (Justifications — Clean Code, Ch. 10)

### 1. Elimination of Conditional/Branching Logic in Favor of Polymorphism
Neither `Calculator` (Context) nor `CalculatorBuilder` uses `if/else` chains or `switch` statements over format type flags (`PLAIN`, `FIXED`, `SCIENTIFIC`). Instead, `Calculator` invokes the strategy polymorphically:
```java
public String calculate(String expression) {
    Objects.requireNonNull(expression, "Expression must not be null");
    if (expression.isBlank()) { return ""; }

    Expr ast = FrontendFacade.compile(expression);

    beforeCalculation(expression);
    Number result = evaluate(ast);
    afterCalculation(result);

    return formatStrategy.format(result);
}
```
Similarly, `CalculatorBuilder` stores a `NumberFormatStrategy` reference directly and reuses immutable strategy constants rather than branching in a factory `switch`.

### 2. Meaningful, Intention-Revealing Names for Each Strategy
Every interface, class, and method name in the `calculator.strategy` package immediately communicates its exact algorithmic role:
- `NumberFormatStrategy` — clearly identifies the Strategy interface for number formatting.
- `PlainDecimalFormatStrategy` — indicates plain, ungrouped decimal output with trailing zeros stripped.
- `FixedDecimalFormatStrategy` — indicates fixed fractional scale formatting.
- `ScientificFormatStrategy` — indicates normalized scientific ($10^n$) notation.

### 3. Single Responsibility Principle (SRP — Clean Code, Ch. 10)
Following *Clean Code, Chapter 10 (Classes)*, classes should be small, cohesive, and have only **one reason to change**:
- `format(Number)` was completely removed from `MathEngine`, `DoubleMathEngine`, and `BigDecimalMathEngine`. Now `MathEngine` classes are strictly responsible for numerical arithmetic.
- Each concrete strategy class encapsulates **one formatting algorithm and nothing else** (`PlainDecimalFormatStrategy` is 12 lines; `ScientificFormatStrategy` is 10 lines; `FixedDecimalFormatStrategy` is 23 lines).

### 4. Context Remains Algorithm-Agnostic (Isolating from Change — Clean Code, Ch. 10)
`Calculator` and `TracedCalculator` depend solely on the `NumberFormatStrategy` abstraction (Dependency Inversion Principle):
- The Context has zero knowledge of whether the underlying strategy uses `BigDecimal.stripTrailingZeros()`, `String.format()`, locale grouping, or custom decimal scale parameters.
- Modifying how `FixedDecimalFormatStrategy` rounds or groups digits has zero impact on `Calculator`.

### 5. Easy Extensibility Demonstrated (Open-Closed Principle — Clean Code, Ch. 10)
*Clean Code, Chapter 10 ("Organizing for Change")* emphasizes structuring classes so the system can be extended without modifying existing code:
- Adding a new formatting algorithm (e.g., `EngineeringFormatStrategy`, `CurrencyFormatStrategy`, or `HexadecimalFormatStrategy`) requires only creating a single class implementing `NumberFormatStrategy`.
- It can be injected immediately via `calculator.setFormatStrategy(new CustomFormatStrategy())` or `builder.format(new CustomFormatStrategy())` **without touching a single line of `Calculator`, `TracedCalculator`, `CalculatorBuilder`, or `CalculatorFacade`**.

---

## 5. Formal Grammar & Compiler Subsystem

The underlying frontend subsystem implements a **Deterministic Recursive Descent Parser** adhering to the following Context-Free Grammar (CFG) in Extended Backus-Naur Form (EBNF):

```ebnf
expression ::= term ( ( "+" | "-" ) term )*
term       ::= unary ( ( "*" | "/" ) unary )*
unary      ::= "-" unary | power                 (* Right-associative prefix negation *)
power      ::= primary ( "^" unary )?            (* Right-associative exponentiation *)
primary    ::= NUMBER | "(" expression ")"
NUMBER     ::= [0-9]+ ( "." [0-9]+ )?
```

### Precedence & Associativity Table

| Precedence      | Operators       | Description                        | Associativity | Subsystem Method      |
|-----------------|-----------------|------------------------------------|---------------|-----------------------|
| **1 (Highest)** | Literals, `( )` | Numbers and sub-expressions        | N/A           | `Parser.primary()`    |
| **2**           | `^`             | Exponentiation (`2 ^ 3 ^ 2 = 512`) | Right-to-Left | `Parser.power()`      |
| **3**           | `-`             | Prefix negation (`-2 ^ 2 = -4`)    | Right-to-Left | `Parser.unary()`      |
| **4**           | `*`, `/`        | Multiplication, Division           | Left-to-Right | `Parser.term()`       |
| **5 (Lowest)**  | `+`, `-`        | Addition, Subtraction              | Left-to-Right | `Parser.expression()` |

### Compiler Diagnostic Errors
When an invalid expression is encountered, the frontend subsystem produces compiler-grade visual diagnostics:
```text
  3 + * 4
      ^
Parse Error: Expected number or '(' but found: '*'
```

---

## 6. Project Directory Structure

```text
src/
└── calculator/
    ├── Main.java                                # Client: Demonstrates runtime Strategy switching
    ├── CalculatorFacade.java                    # Subsystem Facade supporting runtime strategy updates
    ├── CalculatorBuilder.java                   # Fluent builder with conditional-free strategy binding
    ├── CalculationException.java                # Unified domain exception
    │
    ├── strategy/                                # STRATEGY PATTERN: Pluggable Formatting Algorithms
    │   ├── NumberFormatStrategy.java            # Strategy Interface
    │   ├── PlainDecimalFormatStrategy.java      # Concrete Strategy 1: Plain decimal (strips trailing zeros)
    │   ├── FixedDecimalFormatStrategy.java      # Concrete Strategy 2: Grouped fixed decimal places
    │   └── ScientificFormatStrategy.java        # Concrete Strategy 3: Normalized scientific notation
    │
    ├── frontend/                                # Subsystem: Compiler Front-End
    │   ├── FrontendFacade.java                  # Secondary Facade (AST Compilation & Tree Dump)
    │   ├── TokenType.java                       # Token classification enum
    │   ├── Token.java                           # Immutable token record
    │   ├── Lexer.java                           # Lexical character scanner
    │   ├── Parser.java                          # Recursive descent parser
    │   ├── Expr.java                            # Sealed AST hierarchy (Number, Binary, Unary)
    │   ├── SyntaxException.java                 # Visual diagnostic base exception
    │   ├── LexException.java                    # Scanner syntax error
    │   └── ParseException.java                  # Grammar syntax error
    │
    └── bridge/                                  # Subsystem: Arithmetic & Evaluation Engines
        ├── abstraction/
        │   ├── Calculator.java                  # CONTEXT & Bridge Abstraction (delegates to Strategy)
        │   └── TracedCalculator.java            # Refined Abstraction (formats trace steps via Strategy)
        │
        └── implementation/
            ├── MathEngine.java                  # Pure numeric computation SPI (SRP compliant)
            ├── DoubleMathEngine.java            # IEEE-754 hardware floating-point engine
            ├── BigDecimalMathEngine.java        # Arbitrary-precision decimal engine
            └── MathException.java               # Arithmetic evaluation error
```

---

## 7. Build and Run

### Prerequisites
* Java JDK 21 or higher.

### Compilation
From the project root:
```bash
javac -d out --source-path src src/calculator/*.java
```

### Execution
```bash
java -cp out calculator.Main
```

---

## 8. Sample Execution Output

```text
====================================================
  SOFTWARE DESIGN PATTERNS - STRATEGY PATTERN DEMO
====================================================

1. Plain Decimal Format Strategy:
   1234567.8900 + 0.0000 = 1234567.89
   1000000 / 3 = 333333.3333333333333333333333333333
   1 / 8000 = 0.000125

2. Fixed Decimal Format Strategy (2 decimal places):
   1234567.8900 + 0.0000 = 1,234,567.89
   1000000 / 3 = 333,333.33
   1 / 8000 = 0.00

3. Fixed Decimal Format Strategy (4 decimal places):
   1234567.8900 + 0.0000 = 1,234,567.8900
   1000000 / 3 = 333,333.3333
   1 / 8000 = 0.0001

4. Scientific Format Strategy:
   1234567.8900 + 0.0000 = 1.234568E+06
   1000000 / 3 = 3.333333E+05
   1 / 8000 = 1.250000E-04

5. Runtime Strategy Switching on '2 ^ 20 / 7':
   [Plain]      -> 149796.5714285714285714285714285714
   [Fixed (2)]  -> 149,796.57
   [Scientific] -> 1.497966E+05
```
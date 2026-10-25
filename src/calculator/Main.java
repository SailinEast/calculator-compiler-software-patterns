package calculator;

import calculator.bridge.abstraction.Calculator;
import calculator.bridge.implementation.BigDecimalMathEngine;
import calculator.strategy.FixedDecimalFormatStrategy;
import calculator.strategy.PlainDecimalFormatStrategy;
import calculator.strategy.ScientificFormatStrategy;

public class Main {
    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("  SOFTWARE DESIGN PATTERNS - STRATEGY PATTERN DEMO");
        System.out.println("====================================================\n");

        Calculator calculator = new Calculator(new BigDecimalMathEngine());

        String expr1 = "1234567.8900 + 0.0000";
        String expr2 = "1000000 / 3";
        String expr3 = "1 / 8000";

        System.out.println("1. Plain Decimal Format Strategy:");
        calculator.setFormatStrategy(new PlainDecimalFormatStrategy());
        System.out.println("   " + expr1 + " = " + calculator.calculate(expr1));
        System.out.println("   " + expr2 + " = " + calculator.calculate(expr2));
        System.out.println("   " + expr3 + " = " + calculator.calculate(expr3));
        System.out.println();

        System.out.println("2. Fixed Decimal Format Strategy (2 decimal places):");
        calculator.setFormatStrategy(new FixedDecimalFormatStrategy());
        System.out.println("   " + expr1 + " = " + calculator.calculate(expr1));
        System.out.println("   " + expr2 + " = " + calculator.calculate(expr2));
        System.out.println("   " + expr3 + " = " + calculator.calculate(expr3));
        System.out.println();

        System.out.println("3. Fixed Decimal Format Strategy (4 decimal places):");
        calculator.setFormatStrategy(new FixedDecimalFormatStrategy(4));
        System.out.println("   " + expr1 + " = " + calculator.calculate(expr1));
        System.out.println("   " + expr2 + " = " + calculator.calculate(expr2));
        System.out.println("   " + expr3 + " = " + calculator.calculate(expr3));
        System.out.println();

        System.out.println("4. Scientific Format Strategy:");
        calculator.setFormatStrategy(new ScientificFormatStrategy());
        System.out.println("   " + expr1 + " = " + calculator.calculate(expr1));
        System.out.println("   " + expr2 + " = " + calculator.calculate(expr2));
        System.out.println("   " + expr3 + " = " + calculator.calculate(expr3));
        System.out.println();

        String dynamicExpr = "2 ^ 20 / 7";
        System.out.println("5. Runtime Strategy Switching on '" + dynamicExpr + "':");

        calculator.setFormatStrategy(new PlainDecimalFormatStrategy());
        System.out.println("   [Plain]      -> " + calculator.calculate(dynamicExpr));

        calculator.setFormatStrategy(new FixedDecimalFormatStrategy(2));
        System.out.println("   [Fixed (2)]  -> " + calculator.calculate(dynamicExpr));

        calculator.setFormatStrategy(new ScientificFormatStrategy());
        System.out.println("   [Scientific] -> " + calculator.calculate(dynamicExpr));
    }
}
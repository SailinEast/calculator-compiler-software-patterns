package calculator;

public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  SOFTWARE DESIGN PATTERNS - FACADE PATTERN DEMO    ");
        System.out.println("==================================================\n");

        CalculatorFacade calculator = new CalculatorFacade();

        // 1. Standard evaluation
        String expr1 = "10 / 3 + 2 * 4";
        System.out.println("1. Standard Evaluation:");
        System.out.println("   Expr:   " + expr1);
        System.out.println("   Result: " + calculator.calculate(expr1));
        System.out.println();

        // 2. High-precision decimal evaluation
        String precisionExpr = "0.1 + 0.2";
        System.out.println("2. High Precision (50 digits):");
        System.out.println("   Expr:   " + precisionExpr);
        System.out.println("   Result: " + calculator.calculate(precisionExpr, 50));
        System.out.println();

        // 3. Step-by-step diagnostic evaluation
        System.out.println("3. Traced Evaluation:");
        calculator.calculateWithTrace("3 + 4 * (2 - 1) ^ 3");

        // 4. Expression validity checking
        String expr2 = "9 + 3 * (2 + 2) / 0";
        System.out.println("4. Validity checking:");
        System.out.println("   Expression '" + expr1 + "'      is valid: " + calculator.isValid(expr1));
        System.out.println("   Expression '" + expr2 + "' is valid: " + calculator.isValid(expr2));
        System.out.println();

        // 5. Error Handling
        System.out.println("5. Error Handling via Facade:");
        try {
            calculator.calculate("10 + * 2");
        } catch (Exception e) {
            System.out.println("   Caught expected error: " + e.getMessage());
        }
    }
}
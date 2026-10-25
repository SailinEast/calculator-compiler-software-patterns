package calculator.bridge.abstraction;

import calculator.bridge.implementation.MathEngine;
import calculator.frontend.TokenType;
import calculator.strategy.NumberFormatStrategy;

public class TracedCalculator extends Calculator {
    private int stepCount = 0;

    public TracedCalculator(MathEngine engine) {
        super(engine);
    }

    public TracedCalculator(MathEngine engine, NumberFormatStrategy formatStrategy) {
        super(engine, formatStrategy);
    }

    @Override
    protected void beforeCalculation(String expression) {
        stepCount = 0;
        System.out.println("==================================================");
        System.out.println("Tracing Expression: " + expression);
        System.out.println("Active Backend:     " + engine.getName());
        System.out.println("Format Strategy:    " + formatStrategy.getClass().getSimpleName());
        System.out.println("--------------------------------------------------");
    }

    @Override
    protected void onLiteral(String literal, Number val) {
        stepCount++;
        System.out.printf("  Step %02d: [LITERAL] Read '%s' -> %s%n",
                stepCount, literal, formatStrategy.format(val));
    }

    @Override
    protected void onUnary(TokenType op, Number operand, Number result) {
        stepCount++;
        System.out.printf("  Step %02d: [UNARY]   %s(%s) -> %s%n",
                stepCount, op, formatStrategy.format(operand), formatStrategy.format(result));
    }

    @Override
    protected void onBinary(TokenType op, Number left, Number right, Number result) {
        stepCount++;
        System.out.printf("  Step %02d: [BINARY]  %s %s %s = %s%n",
                stepCount, formatStrategy.format(left), op, formatStrategy.format(right), formatStrategy.format(result));
    }

    @Override
    protected void afterCalculation(Number result) {
        System.out.println("--------------------------------------------------");
        System.out.println("Final Result: " + formatStrategy.format(result));
        System.out.println("==================================================\n");
    }
}
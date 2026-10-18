package calculator;

import calculator.bridge.abstraction.Calculator;
import calculator.bridge.implementation.MathException;
import calculator.frontend.SyntaxException;

import java.util.Objects;

public class CalculatorFacade {
    private final CalculatorBuilder builder;
    private final Calculator calculator;
    private final Calculator preciseCalculator;
    private final Calculator tracedCalculator;
    private final Calculator preciseTracedCalculator;

    public CalculatorFacade() {
        this.builder = new CalculatorBuilder();
        this.calculator = builder.build();
        this.preciseCalculator = builder.reset().precise().build();
        this.tracedCalculator = builder.reset().traced().build();
        this.preciseTracedCalculator = builder.reset().precise().traced().build();
    }

    public String calculateDouble(String expression) {
        return validateAndCalculate(expression, calculator);
    }

    public String calculatePrecise(String expression) {
        return validateAndCalculate(expression, preciseCalculator);
    }

    public String calculatePrecise(String expression, int precision) {
        return validateAndCalculate(expression,
                builder.reset().precision(precision).build());
    }

    public String calculateTraced(String expression) {
        return validateAndCalculate(expression, tracedCalculator);
    }

    public String calculateTracedPrecise(String expression) {
        return validateAndCalculate(expression, preciseTracedCalculator);
    }

    public String calculateTracedPrecise(String expression, int precision) {
        return validateAndCalculate(expression,
                builder.reset().precision(precision).traced().build());
    }

    public boolean canCalculate(String expression) {
        if (expression == null) {
            return false;
        }
        try {
            calculator.calculate(expression);
            return true;
        } catch (MathException | SyntaxException e) {
            return false;
        }
    }

    private String validateAndCalculate(String expression, Calculator calculator) {
        Objects.requireNonNull(expression, "Expression cannot be null.");
        try {
            return calculator.calculate(expression);
        } catch (SyntaxException | MathException e) {
            throw new CalculationException("Calculation failed: " + e.getMessage());
        }
    }
}

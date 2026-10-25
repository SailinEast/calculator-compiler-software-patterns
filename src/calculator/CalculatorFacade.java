package calculator;

import calculator.bridge.abstraction.Calculator;
import calculator.bridge.implementation.MathException;
import calculator.frontend.SyntaxException;
import calculator.strategy.NumberFormatStrategy;
import calculator.strategy.PlainDecimalFormatStrategy;

import java.util.Objects;

public class CalculatorFacade {
    private final CalculatorBuilder builder;
    private final Calculator calculator;
    private final Calculator preciseCalculator;
    private final Calculator tracedCalculator;
    private final Calculator preciseTracedCalculator;
    private NumberFormatStrategy currentFormatStrategy;

    public CalculatorFacade() {
        this.builder = new CalculatorBuilder();
        this.currentFormatStrategy = new PlainDecimalFormatStrategy();
        this.calculator = builder.build();
        this.preciseCalculator = builder.reset().precise().build();
        this.tracedCalculator = builder.reset().traced().build();
        this.preciseTracedCalculator = builder.reset().precise().traced().build();
    }

    public void setFormatStrategy(NumberFormatStrategy formatStrategy) {
        this.currentFormatStrategy = Objects.requireNonNull(formatStrategy, "Format strategy cannot be null.");
        this.calculator.setFormatStrategy(formatStrategy);
        this.preciseCalculator.setFormatStrategy(formatStrategy);
        this.tracedCalculator.setFormatStrategy(formatStrategy);
        this.preciseTracedCalculator.setFormatStrategy(formatStrategy);
    }

    public String calculateDouble(String expression) {
        return validateAndCalculate(expression, calculator);
    }

    public String calculatePrecise(String expression) {
        return validateAndCalculate(expression, preciseCalculator);
    }

    public String calculatePrecise(String expression, int precision) {
        return validateAndCalculate(expression,
                builder.reset().precision(precision).format(currentFormatStrategy).build());
    }

    public String calculateTraced(String expression) {
        return validateAndCalculate(expression, tracedCalculator);
    }

    public String calculateTracedPrecise(String expression) {
        return validateAndCalculate(expression, preciseTracedCalculator);
    }

    public String calculateTracedPrecise(String expression, int precision) {
        return validateAndCalculate(expression,
                builder.reset().precision(precision).traced().format(currentFormatStrategy).build());
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

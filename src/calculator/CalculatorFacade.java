package calculator;

import calculator.bridge.abstraction.Calculator;
import calculator.bridge.abstraction.StandardCalculator;
import calculator.bridge.abstraction.TracedCalculator;
import calculator.bridge.implementation.BigDecimalMathEngine;
import calculator.bridge.implementation.DoubleMathEngine;
import calculator.bridge.implementation.MathEngine;
import calculator.frontend.SyntaxException;

import java.util.Objects;

/**
 * A Calculator API that defaults to traceless 64-bit (double) calculation
 */
public class CalculatorFacade {
    private final MathEngine engine;
    private final Calculator calculator;

    public CalculatorFacade() {
        this.engine = new DoubleMathEngine();
        this.calculator = new StandardCalculator(engine);
    }

    public String calculate(String expression) {
        validate(expression);
        try {
            return calculator.calculate(expression);
        } catch (SyntaxException | ArithmeticException e) {
            throw new CalculationException("Calculation failed: " + e.getMessage());
        }
    }

    public String calculate(String expression, int precision) {
        validate(expression);
        try {
            MathEngine preciseEngine = new BigDecimalMathEngine(precision);
            Calculator preciseCalculator = new StandardCalculator(preciseEngine);
            return preciseCalculator.calculate(expression);
        } catch (SyntaxException | ArithmeticException e) {
            throw new CalculationException("Precision calculation failed: " + e.getMessage());
        }
    }

    public String calculateWithTrace(String expression) {
        validate(expression);
        try {
            Calculator tracedCalculator = new TracedCalculator(engine);
            return tracedCalculator.calculate(expression);
        } catch (SyntaxException | ArithmeticException e) {
            throw new CalculationException("Traced calculation failed: " + e.getMessage());
        }
    }

    public String calculateWithTrace(String expression, int precision) {
        validate(expression);
        try {
            MathEngine preciseEngine = new BigDecimalMathEngine(precision);
            Calculator tracedCalculator = new TracedCalculator(preciseEngine);
            return tracedCalculator.calculate(expression);
        } catch (SyntaxException | ArithmeticException e) {
            throw new CalculationException("Traced calculation failed: " + e.getMessage());
        }
    }

    public boolean isValid(String expression) {
        if (expression == null) {
            return false;
        }
        try {
            calculator.calculate(expression);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void validate(String expression) {
        Objects.requireNonNull(expression, "Expression cannot be null.");
    }
}

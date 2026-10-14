package calculator.builder;

import calculator.bridge.abstraction.Calculator;
import calculator.bridge.abstraction.StandardCalculator;
import calculator.bridge.abstraction.TracedCalculator;
import calculator.bridge.implementation.BigDecimalMathEngine;
import calculator.bridge.implementation.DoubleMathEngine;
import calculator.bridge.implementation.MathEngine;

public class CalculatorBuilder implements Builder {
    private Integer precision;
    private boolean traced;

    public CalculatorBuilder() {
        reset();
    }

    @Override
    public void reset() {
        precision = null;
        traced = false;
    }

    @Override
    public CalculatorBuilder withPrecision(int precision) {
        if (precision <= 0) {
            throw new IllegalArgumentException("Precision must be a positive integer, got: " + precision);
        }
        this.precision = precision;
        return this;
    }

    @Override
    public CalculatorBuilder traced() {
        this.traced = true;
        return this;
    }

    @Override
    public Calculator build() {
        MathEngine engine = (precision != null)
                ? new BigDecimalMathEngine(precision)
                : new DoubleMathEngine();
        return traced
                ? new TracedCalculator(engine)
                : new StandardCalculator(engine);
    }
}

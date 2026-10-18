package calculator;

import calculator.bridge.abstraction.Calculator;
import calculator.bridge.abstraction.TracedCalculator;
import calculator.bridge.implementation.BigDecimalMathEngine;
import calculator.bridge.implementation.DoubleMathEngine;
import calculator.bridge.implementation.MathEngine;

public class CalculatorBuilder {
    private static final int UNSET_PRECISION = 0;
    private int precision;
    private boolean traced;
    private boolean precise;

    public CalculatorBuilder reset() {
        precision = UNSET_PRECISION;
        traced = false;
        precise = false;
        return this;
    }

    public CalculatorBuilder precise() {
        this.precise = true;
        return this;
    }

    public CalculatorBuilder precision(int precision) {
        if (precision <= 0) {
            throw new IllegalArgumentException("Precision must be a positive integer, got: " + precision);
        }
        this.precision = precision;
        this.precise = true;
        return this;
    }

    public CalculatorBuilder traced() {
        this.traced = true;
        return this;
    }

    private MathEngine createEngine() {
        if (!precise) return new DoubleMathEngine();

        return (precision == UNSET_PRECISION)
                ? new BigDecimalMathEngine()
                : new BigDecimalMathEngine(precision);
    }

    public Calculator build() {
        MathEngine engine = createEngine();
        return (traced)
                ? new TracedCalculator(engine)
                : new Calculator(engine);
    }
}

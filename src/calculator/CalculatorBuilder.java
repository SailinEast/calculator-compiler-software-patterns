package calculator;

import calculator.bridge.abstraction.Calculator;
import calculator.bridge.abstraction.TracedCalculator;
import calculator.bridge.implementation.BigDecimalMathEngine;
import calculator.bridge.implementation.DoubleMathEngine;
import calculator.bridge.implementation.MathEngine;
import calculator.strategy.FixedDecimalFormatStrategy;
import calculator.strategy.NumberFormatStrategy;
import calculator.strategy.PlainDecimalFormatStrategy;
import calculator.strategy.ScientificFormatStrategy;

import java.util.Objects;

public class CalculatorBuilder {
    private static final int UNSET_PRECISION = 0;

    private static final NumberFormatStrategy PLAIN_FORMAT = new PlainDecimalFormatStrategy();
    private static final NumberFormatStrategy SCIENTIFIC_FORMAT = new ScientificFormatStrategy();
    private static final NumberFormatStrategy DEFAULT_FIXED_FORMAT = new FixedDecimalFormatStrategy();

    private int precision;
    private boolean traced;
    private boolean precise;
    private NumberFormatStrategy formatStrategy = PLAIN_FORMAT;

    public CalculatorBuilder reset() {
        precision = UNSET_PRECISION;
        traced = false;
        precise = false;
        formatStrategy = PLAIN_FORMAT;
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

    public CalculatorBuilder format(NumberFormatStrategy formatStrategy) {
        this.formatStrategy = Objects.requireNonNull(formatStrategy, "Format strategy cannot be null.");
        return this;
    }

    public CalculatorBuilder plainFormat() {
        this.formatStrategy = PLAIN_FORMAT;
        return this;
    }

    public CalculatorBuilder scientific() {
        this.formatStrategy = SCIENTIFIC_FORMAT;
        return this;
    }

    public CalculatorBuilder fixedFormat() {
        this.formatStrategy = DEFAULT_FIXED_FORMAT;
        return this;
    }

    public CalculatorBuilder fixedFormat(int decimalPlaces) {
        this.formatStrategy = new FixedDecimalFormatStrategy(decimalPlaces);
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
                ? new TracedCalculator(engine, formatStrategy)
                : new Calculator(engine, formatStrategy);
    }
}

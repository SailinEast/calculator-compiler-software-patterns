package calculator.bridge.implementation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class BigDecimalMathEngine implements MathEngine {
    private final MathContext mathContext;

    public BigDecimalMathEngine() {
        this.mathContext = new MathContext(34, RoundingMode.HALF_UP);
    }

    public BigDecimalMathEngine(int precision) {
        if (precision <= 0) throw new IllegalArgumentException("Precision must be at least 1.");
        this.mathContext = new MathContext(precision, RoundingMode.HALF_UP);
    }

    @Override
    public String getName() {
        return "Arbitrary-Precision BigDecimal Engine (" + mathContext.getPrecision() + " digits)";
    }

    private BigDecimal toBigDecimal(Number n) {
        if (n instanceof BigDecimal) {
            return (BigDecimal) n;
        }
        return new BigDecimal(n.toString());
    }

    @Override
    public Number parseLiteral(String literal) {
        return new BigDecimal(literal);
    }

    @Override
    public Number add(Number a, Number b) {
        return toBigDecimal(a).add(toBigDecimal(b), mathContext);
    }

    @Override
    public Number subtract(Number a, Number b) {
        return toBigDecimal(a).subtract(toBigDecimal(b), mathContext);
    }

    @Override
    public Number multiply(Number a, Number b) {
        return toBigDecimal(a).multiply(toBigDecimal(b), mathContext);
    }

    @Override
    public Number divide(Number a, Number b) {
        BigDecimal divisor = toBigDecimal(b);
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new MathException("Division by zero in BigDecimalMathEngine");
        }
        return toBigDecimal(a).divide(divisor, mathContext);
    }

    @Override
    public Number power(Number a, Number b) {
        BigDecimal base = toBigDecimal(a);
        BigDecimal exponent = toBigDecimal(b).stripTrailingZeros();

        // If exponent is an integer, use exact BigDecimal.pow()
        if (exponent.scale() <= 0) {
            return base.pow(exponent.intValueExact(), mathContext);
        } else {
            // Fallback for fractional exponents
            double result = Math.pow(base.doubleValue(), exponent.doubleValue());
            if (Double.isNaN(result) || Double.isInfinite(result))
                throw new MathException("Unsupported or undefined operation.");
            return new BigDecimal(Double.toString(result), mathContext);
        }
    }

    @Override
    public Number negate(Number a) {
        return toBigDecimal(a).negate(mathContext);
    }
}
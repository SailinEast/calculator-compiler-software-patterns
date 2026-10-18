package calculator.bridge.implementation;

import calculator.frontend.MathException;

public class DoubleMathEngine implements MathEngine {
    @Override
    public String getName() {
        return "IEEE-754 64-bit Double Engine";
    }

    @Override
    public Number parseLiteral(String literal) {
        return Double.parseDouble(literal);
    }

    @Override
    public Number add(Number a, Number b) {
        return a.doubleValue() + b.doubleValue();
    }

    @Override
    public Number subtract(Number a, Number b) {
        return a.doubleValue() - b.doubleValue();
    }

    @Override
    public Number multiply(Number a, Number b) {
        return a.doubleValue() * b.doubleValue();
    }

    @Override
    public Number divide(Number a, Number b) {
        if (b.doubleValue() == 0.0) {
            throw new MathException("Division by zero in DoubleMathEngine");
        }
        return a.doubleValue() / b.doubleValue();
    }

    @Override
    public Number power(Number a, Number b) {
        double result = Math.pow(a.doubleValue(), b.doubleValue());
        if (Double.isNaN(result) || Double.isInfinite(result))
            throw new MathException("Unsupported or undefined operation.");
        return result;
    }

    @Override
    public Number negate(Number a) {
        return -a.doubleValue();
    }

    @Override
    public String format(Number value) {
        return String.valueOf(value.doubleValue());
    }
}

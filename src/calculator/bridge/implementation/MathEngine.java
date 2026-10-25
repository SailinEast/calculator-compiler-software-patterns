package calculator.bridge.implementation;

public interface MathEngine {
    String getName();
    Number parseLiteral(String literal);
    Number add(Number a, Number b);
    Number subtract(Number a, Number b);
    Number multiply(Number a, Number b);
    Number divide(Number a, Number b);
    Number power(Number a, Number b);
    Number negate(Number a);
}
package calculator.bridge.abstraction;

import calculator.bridge.implementation.MathEngine;
import calculator.frontend.*;

import java.util.Objects;

public class Calculator {
    protected MathEngine engine;

    public Calculator(MathEngine engine) {
        this.engine = Objects.requireNonNull(engine, "MathEngine must not be null");
    }

    public void setEngine(MathEngine engine) {
        this.engine = Objects.requireNonNull(engine, "MathEngine must not be null");
    }

    public MathEngine getEngine() {
        return engine;
    }

    public String calculate(String expression) {
        Objects.requireNonNull(expression, "Expression must not be null");
        if (expression.isBlank()) { return ""; }

        Expr ast = FrontendFacade.compile(expression);

        beforeCalculation(expression);
        Number result = evaluate(ast);
        afterCalculation(result);

        return engine.format(result);
    }

    protected Number evaluate(Expr node) {
        return switch (node) {
            case Expr.Number num -> {
                Number val = engine.parseLiteral(num.getValue());
                onLiteral(num.getValue(), val);
                yield val;
            }

            case Expr.Unary u -> {
                Number operand = evaluate(u.getRight());
                Number result = engine.negate(operand);
                onUnary(u.getOperator(), operand, result);
                yield result;
            }

            case Expr.Binary b -> {
                Number left = evaluate(b.getLeft());
                Number right = evaluate(b.getRight());

                Number result = switch (b.getOperator()) {
                    case PLUS  -> engine.add(left, right);
                    case MINUS -> engine.subtract(left, right);
                    case STAR  -> engine.multiply(left, right);
                    case SLASH -> engine.divide(left, right);
                    case CARET -> engine.power(left, right);
                    default -> throw new UnsupportedOperationException("Unknown operator: " + b.getOperator());
                };

                onBinary(b.getOperator(), left, right, result);
                yield result;
            }
        };
    }

    protected void beforeCalculation(String expression) {}
    protected void afterCalculation(Number result) {}
    protected void onLiteral(String literal, Number val) {}
    protected void onUnary(TokenType op, Number operand, Number result) {}
    protected void onBinary(TokenType op, Number left, Number right, Number result) {}
}
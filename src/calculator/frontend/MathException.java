package calculator.frontend;

public class MathException extends RuntimeException {
    public MathException(String message) {
        super("\n\nMath Error: " + message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}

package calculator.frontend;

import java.util.Objects;

public class FrontendFacade {
    public static Expr compile(String expression) {
        validate(expression);
        return parseExpression(expression);
    }

    public static String getSyntaxTree(String expression) {
        validate(expression);
        return parseExpression(expression).toTreeString();
    }

    private static Expr parseExpression(String expression) {
        Lexer lexer = new Lexer(expression);
        Parser parser = new Parser(expression, lexer.tokenize());
        return parser.parse();
    }

    private static void validate(String expression) {
        Objects.requireNonNull(expression, "Expression cannot be null.");
    }
}

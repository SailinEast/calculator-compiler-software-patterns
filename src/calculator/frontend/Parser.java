package calculator.frontend;

import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private final String source;

    public Parser(String source, List<Token> tokens) {
        this.source = source;
        this.tokens = tokens;
    }

    public Expr parse() {
        if (isAtEnd()) throw new ParseException("", 0, "Cannot parse blank expression.");
        Expr root = expression();
        if (!isAtEnd()) {
            Token token = peek();
            throw new ParseException(
                    source,
                    token.position(),
                    "Unexpected token after expression: '" + token.lexeme() + "'"
            );
        }
        return root;
    }

    // expression -> term ( ( "+" | "-" ) term )*
    private Expr expression() {
        Expr expr = term();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            TokenType op = previous().type();
            Expr right = term();
            expr = new Expr.Binary(expr, op, right);
        }

        return expr;
    }

    // term -> unary ( ( "*" | "/" ) unary )*
    private Expr term() {
        Expr expr = unary();

        while (match(TokenType.STAR, TokenType.SLASH)) {
            TokenType op = previous().type();
            Expr right = unary();
            expr = new Expr.Binary(expr, op, right);
        }

        return expr;
    }

    // unary -> "-" unary | power
    private Expr unary() {
        if (match(TokenType.MINUS)) {
            TokenType op = previous().type();
            Expr right = unary();
            return new Expr.Unary(op, right);
        }

        return power();
    }

    // power -> primary ( "^" unary )?
    private Expr power() {
        Expr expr = primary();

        if (match(TokenType.CARET)) {
            TokenType op = previous().type();
            Expr right = unary();
            expr = new Expr.Binary(expr, op, right);
        }

        return expr;
    }

    // primary -> NUMBER | "(" expression ")"
    private Expr primary() {
        if (match(TokenType.NUMBER)) {
            return new Expr.Number(previous().lexeme());
        }

        if (match(TokenType.LPAREN)) {
            Expr expr = expression();
            consume(TokenType.RPAREN, "Expected ')' after expression.");
            return expr;
        }

        Token token = peek();
        throw new ParseException(
                source,
                token.position(),
                "Expected number or '(' but found: '" + token.lexeme() + "'"
        );
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        Token token = peek();
        throw new ParseException(source, token.position(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}
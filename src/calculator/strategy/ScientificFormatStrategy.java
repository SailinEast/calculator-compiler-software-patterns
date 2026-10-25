package calculator.strategy;

import java.math.BigDecimal;

public class ScientificFormatStrategy implements NumberFormatStrategy {
    @Override
    public String format(Number value) {
        return String.format("%E", new BigDecimal(value.toString()));
    }
}

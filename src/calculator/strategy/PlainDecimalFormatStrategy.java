package calculator.strategy;

import java.math.BigDecimal;

public class PlainDecimalFormatStrategy implements NumberFormatStrategy {
    @Override
    public String format(Number value) {
        return new BigDecimal(value.toString())
                .stripTrailingZeros()
                .toPlainString();
    }
}

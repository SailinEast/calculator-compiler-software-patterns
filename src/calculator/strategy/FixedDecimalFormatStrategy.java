package calculator.strategy;

import java.math.BigDecimal;

public class FixedDecimalFormatStrategy implements NumberFormatStrategy {
    private final int decimalPlaces;

    public FixedDecimalFormatStrategy() {
        decimalPlaces = 2;
    }

    public FixedDecimalFormatStrategy(int decimalPlaces) {
        if (decimalPlaces < 0) {
            throw new IllegalArgumentException("Decimal places cannot be negative.");
        }
        this.decimalPlaces = decimalPlaces;
    }

    @Override
    public String format(Number value) {
        return String.format("%,." + decimalPlaces + "f", new BigDecimal(value.toString()));
    }
}

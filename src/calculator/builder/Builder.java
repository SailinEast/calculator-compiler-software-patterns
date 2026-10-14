package calculator.builder;

import calculator.bridge.abstraction.Calculator;

public interface Builder {
    void reset();
    Builder withPrecision(int precision);
    Builder traced();
    Calculator build();
}

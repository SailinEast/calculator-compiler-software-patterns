package calculator.bridge.abstraction;

import calculator.bridge.implementation.MathEngine;

public class StandardCalculator extends Calculator {
    public StandardCalculator(MathEngine engine) {
        super(engine);
    }
}
package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.logging.LogFormatter;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class CalculatorTest {
    private static Calculator calculator;

    @BeforeAll
    static void setUp() {
        calculator = new Calculator();

        Logger.getGlobal().setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new LogFormatter());
        Logger.getGlobal().addHandler(consoleHandler);
    }

    @Test
    void simpleCalculatorTest() {
        Simplification[] simplifications = new Simplification[]{
                new Simplification(Operation.ADDITION, 8.0),
                new Simplification(Operation.ADDITION, 2.0),
                new Simplification(Operation.ADDITION, 3.0),
                new Simplification(Operation.SUBTRACTION, 4.0),
        };
        double result = calculator.calculate(simplifications);
        Assertions.assertEquals(9.0, result, 0.1);
    }

    @Test
    void simpleCalculationTest2() {
        Simplification[] simplifications = new Simplification[]{
                new Simplification(Operation.ADDITION, 8.0),
                new Simplification(Operation.SUBTRACTION, 2.0),
                new Simplification(Operation.ADDITION, 3.0),
                new Simplification(Operation.MULTIPLICATION, 4.0),
        };
        double result = calculator.calculate(simplifications);
        Assertions.assertEquals(18.0, result, 0.1);
    }

    @Test
    void anotherCalculationTest() {
        Simplification[] simplifications = new Simplification[]{
                new Simplification(Operation.ADDITION, 24),
                new Simplification(Operation.DIVISION, 2.0),
                new Simplification(Operation.DIVISION, 3.0),
                new Simplification(Operation.MULTIPLICATION, 4.0),
        };
        double result = calculator.calculate(simplifications);
        Assertions.assertEquals(16, result, 0.1);
    }

    @Test
    void andAnotherCalculationTest() {
        Simplification[] simplifications = new Simplification[]{
                new Simplification(Operation.ADDITION, 10),
                new Simplification(Operation.DIVISION, 5),
                new Simplification(Operation.MULTIPLICATION, 2),
        };
        double result = calculator.calculate(simplifications);
        Assertions.assertEquals(4, result, 0.1);
    }

}

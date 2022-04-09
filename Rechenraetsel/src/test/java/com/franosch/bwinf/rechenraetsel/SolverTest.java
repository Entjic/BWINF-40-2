package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.logging.LogFormatter;
import com.franosch.bwinf.rechenraetsel.model.Digit;
import com.franosch.bwinf.rechenraetsel.model.Part;
import com.franosch.bwinf.rechenraetsel.model.Riddle;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class SolverTest {
    private static Solver solver;

    @BeforeAll
    static void setUp() {
        solver = new Solver();
        Logger.getGlobal().setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new LogFormatter());
        Logger.getGlobal().addHandler(consoleHandler);
    }

    @Test
    void simpleTest() {
        Part[] parts = new Part[]{
                new Part(Operation.NONE, Digit.FOUR),
                new Part(Operation.NONE, Digit.THREE),
                new Part(Operation.NONE, Digit.TWO),
                new Part(Operation.NONE, Digit.SIX),
        };
        Riddle riddle = new Riddle(parts, 8);
        Set<Operation[]> results = solver.solve(riddle);
        for (Operation[] result : results) {
            Logger.getGlobal().info(Arrays.toString(result));
        }
        Assertions.assertEquals(1, results.size());
    }

    @Test
    public void exampleTest() {
        Part[] parts = new Part[]{
                new Part(Operation.NONE, Digit.FOUR),
                new Part(Operation.NONE, Digit.THREE),
                new Part(Operation.NONE, Digit.TWO),
                new Part(Operation.NONE, Digit.SIX),
                new Part(Operation.NONE, Digit.THREE),
                new Part(Operation.NONE, Digit.NINE),
                new Part(Operation.NONE, Digit.SEVEN),
                new Part(Operation.NONE, Digit.EIGHT),
                new Part(Operation.NONE, Digit.TWO),
                new Part(Operation.NONE, Digit.NINE),
                new Part(Operation.NONE, Digit.FOUR),
                new Part(Operation.NONE, Digit.FOUR),
                new Part(Operation.NONE, Digit.SIX),
                new Part(Operation.NONE, Digit.FOUR),
                new Part(Operation.NONE, Digit.FOUR),
                new Part(Operation.NONE, Digit.FIVE),
        };
        Riddle riddle = new Riddle(parts, 4792);
        Set<Operation[]> operations = solver.solve(riddle);
        for (Operation[] operation : operations) {
            System.out.println(Arrays.toString(operation));
        }
        Assertions.assertEquals(1, operations.size());
    }

}

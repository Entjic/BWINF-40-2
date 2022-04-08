package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.check.SubSumChecker;
import com.franosch.bwinf.rechenraetsel.logging.LogFormatter;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class SubSumCheckerTest {

    private static SubSumChecker subSumChecker;

    @BeforeAll
    static void setUp() {
        subSumChecker = new SubSumChecker();
        Logger.getGlobal().setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new LogFormatter());
        Logger.getGlobal().addHandler(consoleHandler);
    }

    @Test
    void simpleSubSumTest() {
        Simplification[] simplifications = new Simplification[]{
                new Simplification(Operation.ADDITION, 8.0),
                new Simplification(Operation.ADDITION, 2.0),
                new Simplification(Operation.ADDITION, 3.0),
                new Simplification(Operation.SUBTRACTION, 4.0),
        };
        boolean result = subSumChecker.isSubSum(simplifications);
        Assertions.assertFalse(result);
    }


}

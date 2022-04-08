package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.logging.LogFormatter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class SimpleGenerationTest {

    @BeforeAll
    static void setUp() {

        Logger.getGlobal().setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new LogFormatter());
        Logger.getGlobal().addHandler(consoleHandler);
    }

    @Test
    void simpleTest() {
        Generator generator = new Generator(2);
        Logger.getGlobal().info(String.valueOf(generator.generate()));
    }

}

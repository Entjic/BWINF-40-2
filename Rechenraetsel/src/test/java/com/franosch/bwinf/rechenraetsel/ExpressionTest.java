package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.logging.LogFormatter;
import com.franosch.bwinf.rechenraetsel.model.check.Expression;
import com.franosch.bwinf.rechenraetsel.model.check.Variable;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class ExpressionTest {


    @BeforeAll
    static void setUp(){

        Logger.getGlobal().setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new LogFormatter());
        Logger.getGlobal().addHandler(consoleHandler);
    }

    @Test
    void simpleExpressionTest() {
        Variable[] variables = new Variable[]{new Variable(Operation.ADDITION, 'a'),
                new Variable(Operation.ADDITION, 'b'),
                new Variable(Operation.MULTIPLICATION, 'c'),
                new Variable(Operation.SUBTRACTION, 'd')};
        Expression expression = new Expression(variables);
        int[] ints = new int[]{8, 2, 3, 4};
        double result = expression.insert(ints);
        Assertions.assertEquals(10.0, result, 1.0);
    }

}

package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.model.check.Expression;
import com.franosch.bwinf.rechenraetsel.model.check.Variable;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import org.junit.Assert;
import org.junit.Test;

public class ExpressionTests {

    @Test
    public void simpleExpressionTest() {
        Variable[] variables = new Variable[]{new Variable(Operation.ADDITION, 'a'),
                new Variable(Operation.ADDITION, 'b'),
                new Variable(Operation.MULTIPLICATION, 'c'),
                new Variable(Operation.SUBTRACTION, 'd')};
        Expression expression = new Expression(variables);
        int[] ints = new int[]{8, 2, 3, 4};
        double result = expression.insert(ints);
        Assert.assertEquals(10.0, result, 1.0);
    }

}

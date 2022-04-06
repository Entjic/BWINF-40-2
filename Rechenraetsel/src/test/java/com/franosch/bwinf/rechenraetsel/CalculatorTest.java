package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;
import org.junit.Assert;
import org.junit.Test;

public class CalculatorTest {

    @Test
    public void simpleCalculatorTest() {
        Calculator calculator = new Calculator();
        Simplification[] simplifications = new Simplification[]{
                new Simplification(Operation.ADDITION, 8.0),
                new Simplification(Operation.ADDITION, 2.0),
                new Simplification(Operation.ADDITION, 3.0),
                new Simplification(Operation.SUBTRACTION, 4.0),
        };
        double result = calculator.calculate(simplifications);
        Assert.assertEquals(9.0, result, 0.1);
    }

    @Test
    public void simpleCalculationTest2(){

        Calculator calculator = new Calculator();
        Simplification[] simplifications = new Simplification[]{
                new Simplification(Operation.ADDITION, 8.0),
                new Simplification(Operation.SUBTRACTION, 2.0),
                new Simplification(Operation.ADDITION, 3.0),
                new Simplification(Operation.MULTIPLICATION, 4.0),
        };
        double result = calculator.calculate(simplifications);
        Assert.assertEquals(18.0, result, 0.1);
    }

}

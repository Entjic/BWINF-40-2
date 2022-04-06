package com.franosch.bwinf.rechenraetsel.model.check;

import com.franosch.bwinf.rechenraetsel.Calculator;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Expression {
    private final Variable[] variables;

    private final Calculator calculator;

    public Expression(Variable... variables) {
        this.variables = variables;
        this.calculator = new Calculator();
    }


    public double insert(int[] integers) {
        try {
            return apply(integers);
        } catch (ArithmeticException e) {
            // System.out.println("invalid");
            return Integer.MIN_VALUE;
        }
    }

    private double apply(int[] integers) throws ArithmeticException {
        // System.out.println("parts: " + Arrays.toString(parts));
        List<Simplification> simplifications = new ArrayList<>();
        for (int i = 0; i < variables.length; i++) {
            Variable variable = variables[i];
            simplifications.add(new Simplification(variable.operation(), integers[i]));
        }
        return calculator.calculate(simplifications.toArray(new Simplification[0]));
    }

    @Override
    public String toString() {
        return "Expression{" +
                "variables=" + Arrays.toString(variables) +
                '}';
    }

    public Variable[] variables() {
        return variables;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Expression) obj;
        return Objects.equals(this.variables, that.variables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variables);
    }

}

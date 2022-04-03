package com.franosch.bwinf.rechenraetsel.model.check;

import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;

public record Equation(Expression left, Expression right, String stringRepresentation) {

    public boolean satisfies(Simplification a, Simplification b, Simplification c) {
        try {
            double resultLeft = left.insert((int) a.value(), (int) b.value(), (int) c.value());
            double resultRight = right.insert((int) a.value(), (int) b.value(), (int) c.value());
            if (resultLeft != resultRight) return false;
            if (Math.floor(resultLeft) == resultLeft) return true;
        } catch (ArithmeticException ignored) {
        }
        return false;
    }

    @Override
    public String toString() {
        return stringRepresentation;
    }
}

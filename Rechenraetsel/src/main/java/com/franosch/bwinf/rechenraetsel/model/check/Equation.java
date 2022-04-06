package com.franosch.bwinf.rechenraetsel.model.check;

public record Equation(Expression left, Expression right, String stringRepresentation) {

    public boolean satisfies(int[] ints, int result) {
        try {
            double resultLeft = left.insert(ints);
            double resultRight = right.insert(ints);
            if (resultLeft == Integer.MIN_VALUE || resultRight == Integer.MIN_VALUE) return false;
            if (resultLeft != resultRight) return false;
            if (resultLeft != result) return false;
            if (Math.floor(resultLeft) == resultLeft) {
                //System.out.println(left);
                //System.out.println(right);
                //System.out.println("left " + resultLeft);
                //System.out.println("right " + resultRight);
                return true;
            }
        } catch (ArithmeticException ignored) {
        }
        return false;
    }

    public boolean satisfies(int[] ints) {
        try {
            double resultLeft = left.insert(ints);
            double resultRight = right.insert(ints);
            if (resultLeft == Integer.MIN_VALUE || resultRight == Integer.MIN_VALUE) return false;
            if (resultLeft != resultRight) return false;
            if (Math.floor(resultLeft) == resultLeft) {
                //System.out.println(left);
                //System.out.println(right);
                //System.out.println("left " + resultLeft);
                //System.out.println("right " + resultRight);
                return true;
            }
        } catch (ArithmeticException ignored) {
        }
        return false;
    }

    // +a-b+c-d=+a*b-c*d
    @Override
    public String toString() {
        return stringRepresentation;
    }
}

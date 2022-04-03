package com.franosch.bwinf.rechenraetsel.model.operation;

public class Division implements IOperation {
    @Override
    public int apply(int a, int b, boolean bool) {
        if (bool) {
            if (a % b != 0) throw new ArithmeticException();
        }
        return a / b;
    }
}

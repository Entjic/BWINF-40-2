package com.franosch.bwinf.rechenraetsel.model.operation;

public class Division implements IOperation {
    @Override
    public int apply(int a, int b) {
        if(a % b != 0) throw new ArithmeticException();
        return a / b;
    }
}

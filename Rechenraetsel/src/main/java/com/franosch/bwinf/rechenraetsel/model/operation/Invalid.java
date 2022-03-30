package com.franosch.bwinf.rechenraetsel.model.operation;

// Null Pattern
public class Invalid implements IOperation {
    @Override
    public int apply(int a, int b) {
        throw new ArithmeticException();
    }
}

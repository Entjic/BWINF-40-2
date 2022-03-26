package com.franosch.bwinf.rechenraetsel.model.operation;

public class Subtraction implements IOperation {

    @Override
    public int apply(int a, int b) {
        return a - b;
    }
}

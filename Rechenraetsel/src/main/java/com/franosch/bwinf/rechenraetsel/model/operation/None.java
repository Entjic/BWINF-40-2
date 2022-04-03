package com.franosch.bwinf.rechenraetsel.model.operation;

public class None implements IOperation {
    @Override
    public int apply(int a, int b, boolean bool) {
        return b;
    }
}

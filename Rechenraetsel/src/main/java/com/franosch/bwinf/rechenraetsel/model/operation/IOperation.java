package com.franosch.bwinf.rechenraetsel.model.operation;

public interface IOperation {
    int apply(int a, int b, boolean errorIfRuleBroken);
}

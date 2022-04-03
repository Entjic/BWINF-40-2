package com.franosch.bwinf.rechenraetsel.model.check;

import com.franosch.bwinf.rechenraetsel.model.operation.Operation;

import java.util.Arrays;

public record Expression(Variable... variables) {
    public double insert(int a, int b, int c) {
        Variable second = variables[1];
        Variable third = variables[2];
        int applied;
        if (second.operation().equals(Operation.MULTIPLICATION) || second.operation().equals(Operation.DIVISION)) {
            applied = second.operation().apply(a, b);
            applied = third.operation().apply(applied, c);
        } else if (third.operation().equals(Operation.MULTIPLICATION) || third.operation().equals(Operation.DIVISION)) {
            applied = third.operation().apply(b, c);
            applied = second.operation().apply(a, applied);
        } else {
            applied = second.operation().apply(a, b);
            applied = third.operation().apply(applied, c);
        }
        return applied;
    }

    @Override
    public String toString() {
        return "Expression{" +
                "variables=" + Arrays.toString(variables) +
                '}';
    }
}

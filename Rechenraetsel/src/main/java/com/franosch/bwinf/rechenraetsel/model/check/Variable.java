package com.franosch.bwinf.rechenraetsel.model.check;

import com.franosch.bwinf.rechenraetsel.model.operation.Operation;

public record Variable(Operation operation, char letter) {
    @Override
    public String toString() {
        return "Variable{" +
                "operation=" + operation +
                ", letter=" + letter +
                '}';
    }
}

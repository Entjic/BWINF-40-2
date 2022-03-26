package com.franosch.bwinf.rechenraetsel.model;

import com.franosch.bwinf.rechenraetsel.model.operation.Operation;

public record Part(Operation operation,
                   Digit digit) {
    @Override
    public String toString() {
        return "Part{" +
                "operation=" + operation +
                ", digit=" + digit +
                '}';
    }
}

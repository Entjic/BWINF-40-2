package com.franosch.bwinf.rechenraetsel.model.operation;

import com.franosch.bwinf.rechenraetsel.model.Part;

public record Simplification(Operation operation, double value) {
    public static Simplification convert(Part part) {
        Operation operation = part.operation();
        if (operation.equals(Operation.NONE)) operation = Operation.ADDITION;
        return new Simplification(operation, part.digit().getAsInt());
    }
}

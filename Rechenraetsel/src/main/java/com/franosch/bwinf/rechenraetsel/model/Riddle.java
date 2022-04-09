package com.franosch.bwinf.rechenraetsel.model;


import com.franosch.bwinf.rechenraetsel.model.operation.Operation;

import java.util.Arrays;

public record Riddle(Part[] parts, int outcome) {

    public static Riddle merge(Riddle... riddles) {
        Part[] combined = Arrays.stream(riddles).flatMap(riddle -> Arrays.stream(riddle.parts).map(part -> {
            Operation operation = part.operation();
            if (!operation.equals(Operation.NONE)) return part;
            return new Part(Operation.ADDITION, part.digit());
        })).toArray(Part[]::new);
        return new Riddle(combined, Arrays.stream(riddles).mapToInt(Riddle::outcome).sum());
    }

    @Override
    public String toString() {
        return getStringRepresentation(false);
    }

    private String getStringRepresentation(boolean obfuscated) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Part part : parts) {
            if (!obfuscated) {
                switch (part.operation()) {
                    case ADDITION, NONE -> stringBuilder.append("+");
                    case SUBTRACTION -> stringBuilder.append("-");
                    case MULTIPLICATION -> stringBuilder.append("*");
                    case DIVISION -> stringBuilder.append(":");
                }
            } else {
                stringBuilder.append("◯");
            }
            stringBuilder.append(" ").append(part.digit().getAsInt()).append(" ");
        }
        stringBuilder.append("= ").append(outcome);
        return stringBuilder.substring(2);
    }

    public String obfuscated() {
        return getStringRepresentation(true);
    }
}

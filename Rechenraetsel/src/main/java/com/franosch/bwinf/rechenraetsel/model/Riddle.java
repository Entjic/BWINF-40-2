package com.franosch.bwinf.rechenraetsel.model;


public record Riddle(Part[] parts, int outcome) {
    @Override
    public String toString() {
        return getStringRepresentation();
    }

    private String getStringRepresentation() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Part part : parts) {
            switch (part.operation()) {
                case ADDITION, NONE -> stringBuilder.append("+");
                case SUBTRACTION -> stringBuilder.append("-");
                case MULTIPLICATION -> stringBuilder.append("*");
                case DIVISION -> stringBuilder.append(":");
            }
            stringBuilder.append(part.digit().getAsInt());
        }
        stringBuilder.append("=").append(outcome);
        return stringBuilder.toString();
    }
}

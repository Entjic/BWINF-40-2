package com.franosch.bwinf.rechenraetsel.model;


import java.util.Arrays;

public record Riddle(Part[] parts, int outcome) {
    @Override
    public String toString() {
        return "Riddle{" +
                "parts=" + Arrays.toString(parts) +
                ", outcome=" + outcome +
                '}';
    }
}

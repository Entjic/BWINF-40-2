package com.franosch.bwinf.rechenraetsel.model;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public class Riddle {
    private final Part[] digits;
    private final int outcome;

    @Override
    public String toString() {
        return "Riddle{" +
                "digits=" + Arrays.toString(digits) +
                ", outcome=" + outcome +
                '}';
    }
}

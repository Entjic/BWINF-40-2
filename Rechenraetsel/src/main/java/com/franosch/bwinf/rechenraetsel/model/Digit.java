package com.franosch.bwinf.rechenraetsel.model;


import lombok.Getter;
import org.w3c.dom.ls.LSOutput;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public enum Digit {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    INVALID(-1);

    @Getter
    private static final Digit[] values = new Digit[]{TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE};
    private final int asInt;

    Digit(int asInt) {
        this.asInt = asInt;
    }

    public static Digit getRandom() {
        return getRandomInRange(2, 10);
    }

    public static Digit getFromInt(int i) {
        return Arrays.stream(Digit.values).filter(digit -> digit.asInt == i).findAny().get();
    }

    public static Digit getRandomInRange(int from, int to) {
        int i = new Random().nextInt(from, to);
        return Arrays.stream(Digit.values).filter(digit -> digit.asInt == i).findAny().get();
    }

    public static Digit getRandomExcept(Integer... integers) {
        Set<Digit> digits = Arrays.stream(integers).map(Digit::getFromInt).collect(Collectors.toSet());
        return getRandomExcept(digits);
    }

    public static Digit getRandomExcept(Collection<Digit> digits) {
        List<Digit> list = Arrays.stream(Digit.values).filter(digit -> !digits.contains(digit)).toList();
        if(list.size() == 0) return Digit.INVALID;
        int i = new Random().nextInt(0, list.size());
        return list.get(i);
    }

    @Override
    public String toString() {
        return "Digit{" +
                "int=" + asInt +
                '}';
    }
}

package com.franosch.bwinf.rechenraetsel.model;


import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public enum Digit {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9);

    @Getter
    private final int asInt;

     Digit(int asInt){
        this.asInt = asInt;
    }

    public static Digit getRandom(){
         return getRandomInRange(1, 10);
    }

    public static Digit getFromInt(int i){
         return Arrays.stream(Digit.values()).filter(digit -> digit.asInt == i).findAny().get();
    }

    public static Digit getRandomInRange(int from, int to){
        int i = new Random().nextInt(from, to);
        return Arrays.stream(Digit.values()).filter(digit -> digit.asInt == i).findAny().get();
    }

    public static Digit getRandomExcept(Integer... integers){
        Set<Digit> digits = Arrays.stream(integers).map(Digit::getFromInt).collect(Collectors.toSet());
        return getRandomExcept(digits);
    }

    public static Digit getRandomExcept(Collection<Digit> digits){
        List<Digit> list = Arrays.stream(Digit.values()).filter(digit -> !digits.contains(digit)).toList();
        int i = new Random().nextInt(1, list.size());
        return list.get(i);
    }

}

package com.franosch.bwinf.rechenraetsel.model.operation;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public enum Operation {
    INVALID(new Invalid()),
    NONE(new None()),
    ADDITION(new Addition()),
    SUBTRACTION(new Subtraction()),
    MULTIPLICATION(new Multiplication()),
    DIVISION(new Division());

    private final IOperation operation;

    public static Operation[] getValues() {
        return new Operation[]{ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION};
    }

    public int apply(int a, int b) {
        return operation.apply(a, b, true);
    }

    public int apply(int a, int b, boolean errorIfRuleBroken) {
        return operation.apply(a, b, errorIfRuleBroken);
    }

    public static Operation get(String s) {
        return switch (s) {
            case "+" -> Operation.ADDITION;
            case "-" -> Operation.SUBTRACTION;
            case "*" -> Operation.MULTIPLICATION;
            case ":", "/" -> Operation.DIVISION;
            default -> Operation.NONE;
        };
    }

    public static Operation getRandom() {
        List<Operation> list = List.of(ADDITION, ADDITION, SUBTRACTION, DIVISION, DIVISION, MULTIPLICATION, MULTIPLICATION);
        int i = new Random().nextInt(0, list.size());
        return list.get(i);
    }

    public static Operation getRandomPlusMinus() {
        return getRandomExcept(List.of(MULTIPLICATION, DIVISION));
    }


    public static Operation getRandomExcept(Collection<Operation> operations) {
        List<Operation> list = new ArrayList<>(List.of(ADDITION, SUBTRACTION, DIVISION, MULTIPLICATION));
        list.removeAll(operations);
        int i = new Random().nextInt(0, list.size());
        return list.get(i);
    }
}

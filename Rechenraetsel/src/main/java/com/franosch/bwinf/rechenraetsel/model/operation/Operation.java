package com.franosch.bwinf.rechenraetsel.model.operation;

import com.franosch.bwinf.rechenraetsel.model.Digit;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public enum Operation {
    NONE(new None()),
    ADDITION(new Addition()),
    SUBTRACTION(new Subtraction()),
    MULTIPLICATION(new Multiplication()),
    DIVISION(new Division());

    private final IOperation operation;

    public int apply(int a, int b){
        return operation.apply(a, b);
    }

    public static Operation getRandom(){
        List<Operation> list = List.of(ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION);
        int i = new Random().nextInt(1, 4);
        return list.get(i);
    }

    public static Operation getRandomPlusMinus(){
        return getRandomExcept(List.of(MULTIPLICATION, DIVISION));
    }


    public static Operation getRandomExcept(Collection<Operation> operations){
        List<Operation> list = new ArrayList<>(List.of(ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION));
        list.removeAll(operations);
        int i = new Random().nextInt(1, list.size());
        return list.get(i);
    }
}

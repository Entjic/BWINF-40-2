package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.model.Digit;
import com.franosch.bwinf.rechenraetsel.model.Part;
import com.franosch.bwinf.rechenraetsel.model.Riddle;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class Generator {
    private final int length;


    public Riddle generate(){
        Part[] parts = new Part[length];

        for (int i = 0; i < parts.length; i++) {
            if(i == 0){
                parts[0] = new Part(Operation.NONE, Digit.getRandom());
                continue;
            }
            parts[i] = getSuitingPart(parts);
        }

        System.out.println(Arrays.toString(parts));
        return new Riddle(parts, apply(parts));
    }

    private Part getSuitingPart(Part[] parts){
        Part part;
        Set<Digit> digits = new HashSet<>();
        do {
            Digit digit = Digit.getRandomExcept(digits);
            digits.add(digit);
            Set<Operation> operations = new HashSet<>();
            do {
                Operation operation = getNextOperation(operations);
                operations.add(operation);
                part = new Part(operation, digit);
            }while (!isValidPart(parts, part));
            if(part.operation().equals(Operation.NONE)){
                part = new Part(Operation.ADDITION, digit); // kinda cursed ngl
            }
        }while (!isValidPart(parts, part));
        return part;
    }

    private Operation getNextOperation(Set<Operation> operations){
        if(operations.size() == 4) return Operation.NONE;
        return Operation.getRandomExcept(operations);
    }

    private boolean isValidPart(Part[] parts, Part part){
        if(part.operation().equals(Operation.NONE)) return true;
        // validate
        return false;
    }

    private Operation getSuitingOperation(Part[] parts, Digit digit){
        Operation operation = Operation.getRandom();
        while (!validateOperation(parts, operation)) {
            if (digit.equals(Digit.ONE)) {
                operation = Operation.getRandomPlusMinus();
                continue;
            }

        }
    }

    private boolean validateOperation(Part[] parts, Operation operation){

    }

    private int apply(Part[] parts){
        int i = 0;
        for (Part part : parts) {
            i = part.operation().apply(i, part.digit().getAsInt());
        }
        if(i < 0){
            throw new ArithmeticException("value must not be below zero");
        }
        return i;
    }

}

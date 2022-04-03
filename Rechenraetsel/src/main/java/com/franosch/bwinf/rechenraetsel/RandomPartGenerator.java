package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.model.Digit;
import com.franosch.bwinf.rechenraetsel.model.Part;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;

import java.util.HashSet;
import java.util.Set;

public class RandomPartGenerator {
    private final Set<Part> parts;


    public RandomPartGenerator() {
        parts = generateAllParts();
    }

    private Set<Part> generateAllParts() {
        final Set<Part> parts = new HashSet<>();
        for (Operation operation : Operation.getValues()) {
            for (Digit digit : Digit.getValues()) {
                Part part = new Part(operation, digit);
                parts.add(part);
            }
        }
        return parts;
    }

    public Part generate(Set<Part> except) {
        Set<Part> open = new HashSet<>(parts);
        open.removeAll(except);
        if (open.size() == 0) throw new ArithmeticException("no more parts");
        return open.stream().findAny().get();
    }

}

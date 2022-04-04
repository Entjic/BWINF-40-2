package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.model.Digit;
import com.franosch.bwinf.rechenraetsel.model.Part;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;

import java.util.*;

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
        List<Part> open = new ArrayList<>(parts);
        open.removeAll(except);
        if (open.size() == 0) throw new ArithmeticException("no more parts");
        Part part = open.get(new Random().nextInt(0, open.size()));
        // System.out.println("trying " + part);
        return part;
    }

}

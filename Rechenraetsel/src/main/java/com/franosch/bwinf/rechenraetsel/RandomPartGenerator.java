package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.model.Digit;
import com.franosch.bwinf.rechenraetsel.model.Part;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;

import java.util.*;
import java.util.logging.Logger;

public class RandomPartGenerator {
    private final Set<Part> parts;
    private final Logger logger = Logger.getGlobal();

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
        logger.info("trying " + part);
        return part;
    }

}

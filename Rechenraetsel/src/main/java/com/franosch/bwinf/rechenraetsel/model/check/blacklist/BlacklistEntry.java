package com.franosch.bwinf.rechenraetsel.model.check.blacklist;

import com.franosch.bwinf.rechenraetsel.model.Digit;
import com.franosch.bwinf.rechenraetsel.model.Part;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;

import java.util.ArrayList;
import java.util.List;

public class BlacklistEntry {
    private final List<Part> parts;

    public BlacklistEntry(String input) {
        this.parts = parseInput(input);
    }

    public boolean satisfies(Simplification... simplifications) {
        if (simplifications.length != parts.size()) return false;
        for (int i = 0; i < simplifications.length; i++) {
            Simplification simplification = simplifications[i];
            Part part = parts.get(i);
            if (simplification.value() != part.digit().getAsInt()) return false;
            if (!simplification.operation().equals(part.operation())) return false;
        }
        return true;
    }

    private List<Part> parseInput(String input) {
        final List<Part> parts = new ArrayList<>();
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length - 1; i = i + 2) {
            String op = chars[i] + "";
            Operation operation = Operation.get(op);
            String digitString = chars[i + 1] + "";
            Digit digit = Digit.getFromInt(Integer.parseInt(digitString));
            Part part = new Part(operation, digit);
            parts.add(part);
        }
        return parts;
    }

}

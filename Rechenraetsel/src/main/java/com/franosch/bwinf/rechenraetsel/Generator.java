package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.model.Digit;
import com.franosch.bwinf.rechenraetsel.model.Part;
import com.franosch.bwinf.rechenraetsel.model.Riddle;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class Generator {
    private final int length;

    public Riddle generate() {
        Part[] parts = new Part[length];

        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                parts[0] = new Part(Operation.NONE, Digit.getRandom());
                continue;
            }
            parts[i] = getSuitingPart(parts, i);
        }
        return new Riddle(parts, apply(parts));
    }

    private Part getSuitingPart(Part[] parts, int position) {
        Map<Digit, Set<Operation>> map = new HashMap<>();
        for (Digit value : Digit.values()) {
            map.put(value, new HashSet<>());
        }
        Digit digit = Digit.getRandom();
        Operation operation = Operation.getRandom();
        Part part = new Part(operation, digit);

        while (!isValidPart(parts, part, position)) {
            map.get(digit).add(operation);
            part = getNextPart(map);
            // System.out.println(Arrays.toString(parts));
            // System.out.println("trying part " + part);
        }

        return part;
    }

    private boolean isValidPart(Part[] parts, Part part, int position) {
        // System.out.println("parts: " + Arrays.toString(parts));
        // System.out.println("part " + part);
        Part[] copy = Arrays.copyOf(parts, parts.length);
        copy[position] = part;
        if (Arrays.stream(copy).filter(Objects::nonNull).count() == 1) return true;
        try {
            // System.out.println("testing: " + Arrays.toString(copy));
            int i = apply(copy);
            if (i <= 0) {
                // System.out.println("apply below 0");
                return false;
            }
        } catch (ArithmeticException e) {
            // System.out.println("failed apply");
            return false;
        }
        Operation operation = part.operation();
        if (part.digit().equals(Digit.ONE)) {
            int applied = apply(copy);
            if (applied != 0) {
                if (operation.equals(Operation.DIVISION) || operation.equals(Operation.MULTIPLICATION)) return false;
            }
        }
        Part previous = parts[position - 1];
        if (previous.digit().equals(part.digit())) {
            if (operation.equals(Operation.ADDITION) || operation.equals(Operation.SUBTRACTION)) return false;
            if (previous.digit().equals(Digit.TWO)) return false;
            if (previous.operation().equals(Operation.DIVISION) && operation.equals(Operation.MULTIPLICATION))
                return false;
            if (previous.operation().equals(Operation.MULTIPLICATION) && operation.equals(Operation.DIVISION))
                return false;
        }
        if (previous.digit().equals(Digit.FOUR)) {
            if (part.digit().equals(Digit.TWO)) return false;
        }
        if (!passesAdvancedChecks(copy)) {
            // System.out.println("failed advanced checks");
            return false;
        }
        return true;
    }

    private boolean passesAdvancedChecks(Part[] parts) {
        Simplification[] simplifications = reduce(parts);
        List<Part> nonNull = Arrays.stream(parts).filter(Objects::nonNull).toList();
        if (!passesSameSubSumCheck(simplifications)) return false;
        if (parts.length < 3) return true;
        //System.out.println(nonNull);
        for (int i = 1; i < nonNull.size() - 1; i++) {
            Part previous = nonNull.get(i - 1);
            Part current = nonNull.get(i);
            Part next = nonNull.get(i + 1);
            if (previous.digit().equals(next.digit())) {
                // System.out.println(previous);
                //System.out.println(current);
                //System.out.println(next);
                if (current.operation().equals(Operation.MULTIPLICATION) ||
                        current.operation().equals(Operation.DIVISION)) return false;
            }
        }
        return true;
    }

    private boolean passesSameSubSumCheck(Simplification[] simplifications) {
        Set<Simplification[]> combinations = new HashSet<>();
        if (simplifications.length < 2) return true;
        combinations.add(new Simplification[]{simplifications[0]});
        combinations.add(new Simplification[]{simplifications[1]});
        for (int i = 2; i < simplifications.length; i++) {
            Set<Simplification[]> set = new HashSet<>();
            for (Simplification[] combination : combinations) {
                Simplification[] copy = Arrays.copyOf(combination, combination.length + 1);
                copy[combination.length] = simplifications[i];
                set.add(copy);
            }
            combinations.addAll(set);
        }
        Set<Simplification[]> copy = new HashSet<>(combinations);
        for (Simplification[] combination : combinations) {
            copy.remove(combination);
            for (Simplification[] inner : copy) {
                int appliedA = apply(combination);
                int appliedB = apply(inner);
                if (appliedA == appliedB) {
                    // System.out.println("failed same sub sum test");
                    return false;
                }
            }
        }
        return true;
    }

    private Part getNextPart(Map<Digit, Set<Operation>> map) {
        Set<Digit> digits = new HashSet<>();
        Digit digit = Digit.getRandom();
        // System.out.println("random digit" + digit);
        Operation operation = getNextOperation(map.get(digit));
        while (operation.equals(Operation.INVALID)) {
            digits.add(digit);
            digit = getNextDigit(digits);
            if (digit.equals(Digit.INVALID)) throw new ArithmeticException("no possible number left");
            operation = getNextOperation(map.get(digit));
        }
        // System.out.println("next operation " + operation + " digit " + digit);
        return new Part(operation, digit);
    }

    private Digit getNextDigit(Set<Digit> digits) {
        if (digits.size() == 9) return Digit.INVALID;
        return Digit.getRandomExcept(digits);
    }

    private Operation getNextOperation(Set<Operation> operations) {
        if (operations.size() == 4) return Operation.INVALID;
        return Operation.getRandomExcept(operations);
    }

    private int apply(Part[] parts) {
        // System.out.println("parts: " + Arrays.toString(parts));
        Simplification[] out = reduce(parts);
        // System.out.println("out: " + out);
        int applied = 0;
        for (Simplification simplification : out) {
            applied = simplification.operation().apply(applied, simplification.value());
        }
        // System.out.println("applied " + applied);
        return applied;
    }

    private int apply(Simplification[] simplifications) {
        int applied = 0;
        for (Simplification simplification : simplifications) {
            applied = simplification.operation().apply(applied, simplification.value());
        }
        // System.out.println("applied " + applied);
        return applied;
    }

    private Simplification[] reduce(Part[] parts) {
        Stack<Simplification> simplifications = new Stack<>();
        for (int i = parts.length - 1; i >= 0; i--) {
            Part part = parts[i];
            if (part == null) continue;
            simplifications.push(new Simplification(part.operation(), part.digit().getAsInt()));
        }
        List<Simplification> out = new ArrayList<>();
        // System.out.println(simplifications);
        while (!simplifications.empty()) {
            Simplification simplification = simplifications.pop();
            if (simplifications.isEmpty()) {
                out.add(simplification);
                break;
            }
            Simplification next = simplifications.peek();
            // System.out.println("current " + simplification);
            // System.out.println("next " + next);
            if (next.operation().equals(Operation.MULTIPLICATION) || next.operation().equals(Operation.DIVISION)) {
                simplifications.pop();
                Simplification result = new Simplification(simplification.operation(), next.operation().apply(simplification.value(), next.value()));
                // System.out.println("adding " + result);
                simplifications.add(result);
                continue;
            }
            out.add(simplification);
        }
        return out.toArray(new Simplification[0]);
    }

}

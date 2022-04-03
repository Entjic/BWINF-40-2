package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.equationcheck.EquationChecker;
import com.franosch.bwinf.rechenraetsel.model.Digit;
import com.franosch.bwinf.rechenraetsel.model.Part;
import com.franosch.bwinf.rechenraetsel.model.Riddle;
import com.franosch.bwinf.rechenraetsel.model.Triple;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;

import java.util.*;

public class Generator {
    private final int length;
    private final EquationChecker equationChecker;
    private final RandomPartGenerator randomPartGenerator;

    public Generator(int length) {
        this.length = length;
        this.equationChecker = new EquationChecker();
        this.randomPartGenerator = new RandomPartGenerator();
    }

    public Riddle generate() {
        Part[] parts = new Part[length];

        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                parts[0] = new Part(Operation.NONE, Digit.getRandom());
                continue;
            }
            parts[i] = getSuitingPart(parts, i);
        }
        Riddle riddle = new Riddle(parts, apply(parts));
        // System.out.println(riddle);
        return riddle;
    }

    private Part getSuitingPart(Part[] parts, int position) {
        Set<Part> used = new HashSet<>();
        Part part;
        while (true) {
            part = randomPartGenerator.generate(used);
            System.out.println(Arrays.toString(parts));
            System.out.println("trying part " + part);
            if (isValidPart(parts, part, position)) break;
            used.add(part);
        }
        // System.out.println("valid part " + part);
        return part;
    }

    private boolean isValidPart(Part[] parts, Part part, int position) {
        // System.out.println("parts: " + Arrays.toString(parts));
        // System.out.println("part " + part);
        Part[] copy = Arrays.copyOf(parts, parts.length);
        copy[position] = part;
        if (Arrays.stream(copy).filter(Objects::nonNull).count() == 1) {
            // System.out.println(Arrays.toString(copy));
            return true;
        }
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

        if (previous.digit().getAsInt() % part.digit().getAsInt() == 0 && previous.digit().getAsInt() / 2 == part.digit().getAsInt())
            return false;
        if (previous.digit().equals(part.digit())) {
            if (operation.equals(Operation.ADDITION) || operation.equals(Operation.SUBTRACTION)) return false;
            if (previous.digit().equals(Digit.TWO)) return false;
            if (previous.operation().equals(Operation.DIVISION) && operation.equals(Operation.MULTIPLICATION))
                return false;
            if (previous.operation().equals(Operation.MULTIPLICATION) && operation.equals(Operation.DIVISION))
                return false;
        }
        if (!passesCompleteReducedTests(copy)) {
            // System.out.println("failed advanced checks");
            return false;
        }

        if (!passesPartlyReducedTests(copy)) {
            return false;
        }
        // System.out.println("seemingly valid " + part);
        return true;
    }

    private boolean passesPartlyReducedTests(Part[] parts) {
        Triple[] triples = reduceMinusOne(parts);
        // System.out.println("reduced " + Arrays.toString(simplifications));
        return checkSimplified(triples);
    }

    private boolean passesCompleteReducedTests(Part[] parts) {
        Simplification[] simplifications = reduce(parts, true);
        // System.out.println("non null " + nonNull);
        if (!passesSameSubSumCheck(simplifications)) {
            System.out.println("failed due to sub sum");
            return false;
        }
        if (simplifications.length < 3) return true;
        //System.out.println(nonNull);

        if (Arrays.stream(simplifications).anyMatch(simplification -> simplification.value() == 1)) return false;
        // System.out.println(nonNull.size());
        Triple[] triples = new Triple[simplifications.length - 2];
        for (int i = 0; i < simplifications.length - 2; i++) {
            triples[i] = new Triple(simplifications[i], simplifications[i + 1], simplifications[i + 2]);
        }
        return checkSimplified(triples);
    }

    private boolean checkSimplified(Triple[] triples) {
        for (Triple triple : triples) {
            Simplification left = triple.previous();
            Simplification mid = triple.current();
            Simplification right = triple.next();
            if (isABA(left, mid, right)) return false; // vermutlich unnötig
            if (equationChecker.satisfiesEquation(left, mid, right)) return false;
        }
        return true;
    }

    private boolean isABA(Simplification a, Simplification b, Simplification c) {
        if (a.value() == c.value()) {
            System.out.println(a);
            System.out.println(b);
            System.out.println(c);
            if (b.operation().equals(Operation.MULTIPLICATION) || b.operation().equals(Operation.DIVISION)) return true;
            if (c.operation().equals(Operation.MULTIPLICATION) || c.operation().equals(Operation.DIVISION)) return true;
        }
        return false;
    }


    private boolean passesSameSubSumCheck(Simplification[] simplifications) {
        // System.out.println("simplification" + Arrays.toString(simplifications));
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
                if (appliedA * -1 == appliedB) {
                    // System.out.println("a" + Arrays.toString(combination) + " -> " + appliedA);
                    // System.out.println("b" + Arrays.toString(inner) + " -> " + appliedB);
                    // System.out.println("failed same sub sum test");
                    return false;
                }
            }
        }
        return true;
    }

    private int apply(Part[] parts) {
        // System.out.println("parts: " + Arrays.toString(parts));
        Simplification[] out = reduce(parts, true);
        // System.out.println("out: " + out);
        int applied = 0;
        for (Simplification simplification : out) {
            applied = simplification.operation().apply(applied, (int) simplification.value());
        }
        // System.out.println("applied " + applied);
        return applied;
    }

    private int apply(Simplification[] simplifications) {
        int applied = 0;
        for (Simplification simplification : simplifications) {
            applied = simplification.operation().apply(applied, (int) simplification.value());
        }
        // System.out.println("applied " + applied);
        return applied;
    }

    private Triple[] reduceMinusOne(Part[] parts) {
        Part[] nonNull = Arrays.stream(parts).filter(Objects::nonNull).toArray(Part[]::new);
        if (nonNull.length < 3) return new Triple[0];
        List<Triple> output = new ArrayList<>();
        for (int i = 0; i < nonNull.length - 2; i++) {
            System.out.println(Arrays.toString(nonNull));
            Simplification left = reduceLeft(nonNull, i);
            Part current = nonNull[i + 1];
            Simplification mid = new Simplification(current.operation(), current.digit().getAsInt());
            Simplification right = reduceRight(nonNull, i + 2);
            Triple triple = new Triple(left, mid, right);
            System.out.println("generated triple " + triple);
            output.add(triple);
        }
        return output.toArray(new Triple[0]);
    }

    private Simplification reduceRight(Part[] parts, int right) {
        if (right == parts.length - 1) {
            Part currentPart = parts[parts.length - 1];
            return new Simplification(currentPart.operation(), currentPart.digit().getAsInt());
        }
        Part[] rightSlice = Arrays.copyOfRange(parts, right, parts.length);
        System.out.println("right slice " + Arrays.toString(rightSlice));
        Simplification[] reduced = reduce(rightSlice, false);
        return reduced[0];
    }

    private Simplification reduceLeft(Part[] parts, int left) {
        if (left == 0) {
            Part currentPart = parts[0];
            return new Simplification(currentPart.operation(), currentPart.digit().getAsInt());
        }
        Part[] leftSlice = Arrays.copyOfRange(parts, 0, left + 1);
        Simplification[] reduced = reduce(leftSlice, false);
        return reduced[reduced.length - 1];
    }

    private Simplification[] reduce(Part[] parts, boolean errorIfRuleBroken) {
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
                Simplification result = new Simplification(simplification.operation(), next.operation().apply((int) simplification.value(), (int) next.value(), errorIfRuleBroken));
                // System.out.println("adding " + result);
                simplifications.add(result);
                continue;
            }
            out.add(simplification);
        }
        return out.toArray(new Simplification[0]);
    }

}

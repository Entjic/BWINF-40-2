package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.check.BlacklistChecker;
import com.franosch.bwinf.rechenraetsel.check.EquationChecker;
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
    private final BlacklistChecker blacklistChecker;
    private final RandomPartGenerator randomPartGenerator;

    public Generator(int length) {
        this.length = length;
        this.equationChecker = new EquationChecker();
        this.blacklistChecker = new BlacklistChecker();
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
        // System.out.println(Arrays.toString(parts));
        while (true) {
            if (used.size() == 32) {
                // System.out.println(Arrays.toString(parts));
            }
            part = randomPartGenerator.generate(used);
            //System.out.println(Arrays.toString(parts));
            //System.out.println("trying part " + part);
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
        long length = Arrays.stream(copy).filter(Objects::nonNull).count();
        if (length == 1) {
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
        int applied = apply(copy);
        if (part.digit().equals(Digit.ONE)) {
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
        if (length == 2) {
            int a = copy[1].operation().apply(copy[0].digit().getAsInt(), copy[1].digit().getAsInt(), true);
            if (a == 1) return false;
        }
        int[] ints = Arrays.stream(copy).filter(Objects::nonNull).mapToInt(value -> value.digit().getAsInt()).toArray();
        if (ints.length == this.length) {
            if (equationChecker.satisfiesEquation(ints, applied)) {
                // System.out.println("failed naive final equation check");
                return false;
            }
        }else {
            if(equationChecker.satisfiesEquation(ints)){
               // System.out.println("failed naive equation check");
            }
        }

        if (!passesCompleteReducedTests(copy)) {
            // System.out.println("failed completely reduced tests");
            return false;
        }

        if (!passesPartlyReducedTests(copy)) {
            // System.out.println("failed partly reduced tests");
            return false;
        }
        return true;
    }

    private boolean passesPartlyReducedTests(Part[] parts) {
        Triple[] triples = reduceMinusOne(parts);
        return checkSimplified(triples);
    }

    private boolean passesCompleteReducedTests(Part[] parts) {
        Simplification[] simplifications = reduce(parts, true);
        // System.out.println("non null " + nonNull);
        if (simplifications.length < 3) return true;
        //System.out.println(nonNull);

        // System.out.println(nonNull.size());
        Triple[] triples = new Triple[simplifications.length - 2];
        for (int i = 0; i < simplifications.length - 2; i++) {
            Triple triple = new Triple(simplifications[i], simplifications[i + 1], simplifications[i + 2]);
            triples[i] = triple;
        }
        return checkSimplified(triples);
    }

    private boolean checkSimplified(Triple[] triples) {
        for (Triple triple : triples) {
            // System.out.println(triple);
            Simplification left = triple.previous();
            Simplification mid = triple.current();
            Simplification right = triple.next();
            if (isABA(left, mid, right)) {
                // System.out.println("failed to aba");
                return false;
            }
            if (isMultipleDivision(left, mid, right)) {
                // System.out.println("failed to multiple division");
                return false;
            }
            if (isOneAtAnyMoment(left, mid, right)) {
                // System.out.println("failed to one at a moment");
                return false;
            }
            if (isSubSum(left, mid, right)) {
                // System.out.println("failed to sub sum");
                return false;
            }
            if (equationChecker.satisfiesEquation(left, mid, right)) {
                // System.out.println("failed to equations");
                return false;
            }
            if (blacklistChecker.matchesBlacklistedEntry(left, mid, right)) {
                // System.out.println("failed to blacklist");
                return false;
            }
        }
        return true;
    }

    private boolean isOneAtAnyMoment(Simplification left, Simplification mid, Simplification right) {
        if (mid.operation().equals(Operation.MULTIPLICATION) || mid.operation().equals(Operation.DIVISION)) {
            double x = mid.operation().apply((int) left.value(), (int) mid.value(), false);
            if (x == 1) return true;
            if (right.operation().equals(Operation.MULTIPLICATION) || right.operation().equals(Operation.DIVISION)) {
                double y = right.operation().apply((int) x, (int) right.value(), false);
                if (y == 1) return true;
            }
        }
        if (right.operation().equals(Operation.MULTIPLICATION) || right.operation().equals(Operation.DIVISION)) {
            double y = right.operation().apply((int) mid.value(), (int) right.value(), false);
            if (y == 1) return true;
        }
        return false;
    }

    private boolean isMultipleDivision(Simplification left, Simplification mid, Simplification right) {
        if (mid.operation().equals(Operation.DIVISION)) {
            if (left.value() == mid.value() * 2) return true;
            if (right.operation().equals(Operation.DIVISION)) {
                double x = mid.operation().apply((int) left.value(), (int) mid.value(), false);
                if (x == 2 * right.value()) return true;
            }
        }
        if (right.operation().equals(Operation.DIVISION)) {
            if (mid.value() == right.value() * 2) return true;
        }
        return false;
    }

    private boolean isABA(Simplification a, Simplification b, Simplification c) {
        return a.value() == c.value();
    }


    private boolean isSubSum(Simplification... simplifications) {
        // System.out.println("simplification" + Arrays.toString(simplifications));
        Set<Simplification[]> combinations = new HashSet<>();
        if (simplifications.length < 2) return false;
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
                int appliedA = apply(combination, false);
                int appliedB = apply(inner, false);
                if (appliedA * -1 == appliedB) { // FIXME: 03.04.2022 das könnte ein problem sein
                    // System.out.println("a" + Arrays.toString(combination) + " -> " + appliedA);
                    // System.out.println("b" + Arrays.toString(inner) + " -> " + appliedB);
                    // System.out.println("failed same sub sum test");
                    return true;
                }
            }
        }
        return false;
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

    private int apply(Simplification[] simplifications, boolean bool) {
        int applied = 0;
        for (Simplification simplification : simplifications) {
            applied = simplification.operation().apply(applied, (int) simplification.value(), bool);
        }
        // System.out.println("applied " + applied);
        return applied;
    }

    private Triple[] reduceMinusOne(Part[] parts) {
        Part[] nonNull = Arrays.stream(parts).filter(Objects::nonNull).toArray(Part[]::new);
        if (nonNull.length < 3) return new Triple[0];
        List<Triple> output = new ArrayList<>();
        //   System.out.println(Arrays.toString(nonNull));
        for (int i = 0; i < nonNull.length - 2; i++) {
            //System.out.println(Arrays.toString(nonNull));
            Simplification left = reduceLeft(nonNull, i);
            Part current = nonNull[i + 1];
            Simplification mid = new Simplification(current.operation(), current.digit().getAsInt());
            Simplification right = reduceRight(nonNull, i + 2);
            Triple triple = new Triple(left, mid, right);
            //        System.out.println("generated triple " + triple);
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
        //  System.out.println("right slice " + Arrays.toString(rightSlice));
        Simplification[] reduced = reduce(rightSlice, false);
        return reduced[0];
    }

    private Simplification reduceLeft(Part[] parts, int left) {
        if (left == 0) {
            Part currentPart = parts[0];
            Operation operation = currentPart.operation();
            if (operation.equals(Operation.NONE)) operation = Operation.ADDITION;
            return new Simplification(operation, currentPart.digit().getAsInt());
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
            Operation operation = part.operation();
            if (operation.equals(Operation.NONE)) operation = Operation.ADDITION;
            simplifications.push(new Simplification(operation, part.digit().getAsInt()));
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
                Simplification result;
                if (simplification.operation().equals(Operation.DIVISION)) {
                    result = new Simplification(simplification.operation(), Operation.MULTIPLICATION.apply((int) simplification.value(), (int) next.value(), errorIfRuleBroken));
                } else {
                    result = new Simplification(simplification.operation(), next.operation().apply((int) simplification.value(), (int) next.value(), errorIfRuleBroken));
                }
                // System.out.println("adding " + result);
                simplifications.add(result);
                continue;
            }
            out.add(simplification);
        }
        return out.toArray(new Simplification[0]);
    }

}

package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.check.BlacklistChecker;
import com.franosch.bwinf.rechenraetsel.check.EquationChecker;
import com.franosch.bwinf.rechenraetsel.check.SubSumChecker;
import com.franosch.bwinf.rechenraetsel.model.Digit;
import com.franosch.bwinf.rechenraetsel.model.Part;
import com.franosch.bwinf.rechenraetsel.model.Riddle;
import com.franosch.bwinf.rechenraetsel.model.Triple;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;

import java.util.*;
import java.util.logging.Logger;

public class Generator {
    private final int length;
    private final EquationChecker equationChecker;
    private final BlacklistChecker blacklistChecker;
    private final RandomPartGenerator randomPartGenerator;
    private final Calculator calculator;
    private final SubSumChecker subSumChecker;
    private final Logger logger = Logger.getGlobal();

    public Generator(int length) {
        this.length = length;
        this.equationChecker = new EquationChecker();
        this.blacklistChecker = new BlacklistChecker();
        this.randomPartGenerator = new RandomPartGenerator();
        this.calculator = new Calculator();
        this.subSumChecker = new SubSumChecker();
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
        return new Riddle(parts, (int) calculator.calculate(convert(parts)));
    }

    private Part getSuitingPart(Part[] parts, int position) {
        Set<Part> used = new HashSet<>();
        Part part;
        logger.info(Arrays.toString(parts));
        while (true) {
            if (used.size() == 32) {
                // System.out.println(Arrays.toString(parts));
            }
            part = randomPartGenerator.generate(used);
            logger.info(Arrays.toString(parts));
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
        Simplification[] simplifications = convert(copy);

        if (!passesBasicTests(simplifications)) {
            logger.info("failed to basic checks");
            return false;
        }
        Operation operation = part.operation();
        int applied = (int) calculator.calculate(simplifications);
        if (part.digit().equals(Digit.ONE)) {
            if (operation.equals(Operation.DIVISION) || operation.equals(Operation.MULTIPLICATION)) return false;

        }
        Part previous = parts[position - 1];

        if (previous.digit().getAsInt() % part.digit().getAsInt() == 0 && previous.digit().getAsInt() / 2 == part.digit().getAsInt()) {
            // TODO: 07.04.2022 hier könnte man theoretisch nur subtraction und division filtern um insgesamt weniger parts zu filtern auf kosten von ein paar mehr ausnahmen
            logger.info("failed to naive multiple");
            return false;
        }
        if (previous.digit().equals(part.digit())) {
            if (operation.equals(Operation.ADDITION) || operation.equals(Operation.SUBTRACTION)) return false;
            if (previous.digit().equals(Digit.TWO)) return false;
            if (previous.operation().equals(Operation.DIVISION) && operation.equals(Operation.MULTIPLICATION))
                return false;
            if (previous.operation().equals(Operation.MULTIPLICATION) && operation.equals(Operation.DIVISION))
                return false;
        }
        int[] ints = Arrays.stream(simplifications).mapToInt(value -> (int) value.value()).toArray();
        if (ints.length == this.length) {
            if (equationChecker.satisfiesEquation(ints, applied)) {
                logger.info("failed naive final equation check");
                return false;
            }
        } else {
            if (equationChecker.satisfiesEquation(ints)) {
                logger.info("failed naive equation check");
                return false;
            }
        }

        if (!passesCompleteReducedTests(simplifications)) {
            logger.info("failed completely reduced tests");
            return false;
        }

        if (!passesPartlyReducedTests(simplifications)) {
            logger.info("failed partly reduced tests");
            return false;
        }
        return true;
    }

    private boolean passesBasicTests(Simplification... simplifications) {
        try {
            double i = calculator.calculate(true, simplifications);
            if (i != Math.floor(i)) return false;
            if (i <= 1) {
                return false;
            }
        } catch (ArithmeticException e) {
            return false;
        }
        return true;
    }

    private boolean passesPartlyReducedTests(Simplification... simplifications) {
        Triple[] triples = reduceMinusOne(simplifications);
        logger.info(Arrays.toString(triples));
        return checkSimplified(triples);
    }

    private boolean passesCompleteReducedTests(Simplification[] parts) {
        Simplification[] simplifications = calculator.reduce(true, parts);
        // System.out.println("non null " + nonNull);
        if (simplifications.length < 3) return true;
        //System.out.println(nonNull);

        // System.out.println(nonNull.size());
        Triple[] triples = new Triple[simplifications.length - 2];
        for (int i = 0; i < simplifications.length - 2; i++) {
            Triple triple = new Triple(simplifications[i], simplifications[i + 1], simplifications[i + 2]);
            triples[i] = triple;
        }

        for (Triple triple : triples) {
            if (subSumChecker.isSubSum(triple.previous(), triple.current(), triple.next())) return false;
        }

        return checkSimplified(triples);
    }

    private boolean checkSimplified(Triple[] triples) {
        for (Triple triple : triples) {
            Simplification left = triple.previous();
            Simplification mid = triple.current();
            Simplification right = triple.next();
            if (isABA(left, mid, right)) {
                logger.info(String.valueOf(triple));
                logger.info("failed to aba");
                return false;
            }
            if (isMultipleDivision(left, mid, right)) {
                logger.info(String.valueOf(triple));
                logger.info("failed to multiple division");
                return false;
            }
            if (isOneAtAnyMoment(left, mid, right)) {
                logger.info(String.valueOf(triple));
                logger.info("failed to one at a moment");
                return false;
            }
            if (equationChecker.satisfiesEquation(left, mid, right)) {
                logger.info(String.valueOf(triple));
                logger.info("failed to equations");
                return false;
            }
            if (blacklistChecker.matchesBlacklistedEntry(left, mid, right)) {
                logger.info(String.valueOf(triple));
                logger.info("failed to blacklist");
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
            Simplification[] reduced = calculator.reduce(false, left, mid);
            if (reduced[reduced.length - 1].value() == right.value() * 2) return true;
        }
        return false;
    }

    private boolean isABA(Simplification a, Simplification b, Simplification c) {
        return a.value() == c.value();
    }

    private Simplification[] convert(Part[] parts) {
        return Arrays.stream(parts).filter(Objects::nonNull).map(Simplification::convert).toList().toArray(new Simplification[0]);
    }


    private Triple[] reduceMinusOne(Simplification... simplifications) {
        if (simplifications.length < 3) return new Triple[0];
        List<Triple> output = new ArrayList<>();
        //   System.out.println(Arrays.toString(nonNull));
        for (int i = 0; i < simplifications.length - 2; i++) {
            //System.out.println(Arrays.toString(nonNull));
            Simplification left = reduceLeft(simplifications, i);
            Simplification mid = simplifications[i + 1];
            Simplification right = reduceRight(simplifications, i + 2);
            Triple triple = new Triple(left, mid, right);
            output.add(triple);
        }
        return output.toArray(new Triple[0]);
    }

    private Simplification reduceRight(Simplification[] simplifications, int right) {
        if (right == simplifications.length - 1) {
            return simplifications[simplifications.length - 1];
        }
        Simplification[] rightSlice = Arrays.copyOfRange(simplifications, right, simplifications.length);
        //  System.out.println("right slice " + Arrays.toString(rightSlice));
        Simplification[] reduced = calculator.reduce(false, rightSlice);
        return reduced[0];
    }

    private Simplification reduceLeft(Simplification[] simplifications, int left) {
        if (left == 0) {
            return simplifications[0];
        }
        Simplification[] leftSlice = Arrays.copyOfRange(simplifications, 0, left + 1);
        Simplification[] reduced = calculator.reduce(false, leftSlice);
        return reduced[reduced.length - 1];
    }

}

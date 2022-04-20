package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.check.SubSumChecker;
import com.franosch.bwinf.rechenraetsel.model.Digit;
import com.franosch.bwinf.rechenraetsel.model.Part;
import com.franosch.bwinf.rechenraetsel.model.Riddle;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;

import java.util.*;

public class GeneratorV2 {

    public Riddle generate() {
        Digit latest = Digit.ONE;
        List<List<Part>> parts = new ArrayList<>();
        List<Part> moreParts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Digit digit = Digit.getRandomExcept(Collections.singleton(latest));
            latest = digit;
            Operation operation = Operation.MULTIPLICATION;
            moreParts.add(new Part(operation, digit));
        }

        for (int i = 0; i < 2; i++) {
            List<Part> inner = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                Digit digit = Digit.getRandomExcept(Collections.singleton(latest));
                latest = digit;
                Operation operation;
                if (j != 0) {
                    operation = Operation.MULTIPLICATION;
                } else {
                    operation = Operation.getRandomExcept(List.of(Operation.DIVISION));
                }
                Part part = new Part(operation, digit);
                inner.add(part);
            }
            parts.add(inner);
        }
        parts.add(createFive(latest));
        List<Part> out = new ArrayList<>();
        out.addAll(moreParts);
        for (List<Part> part : parts) {
            out.addAll(part);
        }
        Part part = out.get(0);
        out.remove(0);
        Part first = new Part(Operation.ADDITION, part.digit());
        out.add(0, first);
        Calculator calculator = new Calculator();
        Simplification[] simplifications = out.stream().map(Simplification::convert).toArray(Simplification[]::new);
        //System.out.println(out);
        //System.out.println(Arrays.toString(simplifications));

        double applied = calculator.calculate(simplifications);
        Riddle riddle = new Riddle(out.toArray(Part[]::new), (int) applied);
        if (riddle.outcome() < 1) {
            return generate();
        }
        Solver solver = new Solver();
        Set<Operation[]> operations = solver.solve(riddle);
        boolean valid = operations.size() == 1;
        System.out.println(riddle + " is " + valid);
        if (!valid) {
            System.out.println(operations.size());
            System.out.println(Main.getEquationRepresentation(riddle, operations));
        }
        if(!valid){
            return generate();
        }
        return riddle;
    }

    private static boolean isValid(Riddle riddle) {
        Solver solver = new Solver();
        SubSumChecker subSumChecker = new SubSumChecker();
        Calculator calculator = new Calculator();
        Simplification[] simplifications = calculator.reduce(false, convert(riddle.parts()));
        if (subSumChecker.isSubSum(simplifications)) return false;
        return solver.solve(riddle).size() == 1;
    }

    private static Simplification[] convert(Part[] parts) {
        return Arrays.stream(parts).filter(Objects::nonNull).map(Simplification::convert).toList().toArray(new Simplification[0]);
    }

    private static List<Part> createFive(Digit last) {
        Digit a = Digit.getRandomExcept(Collections.singleton(last));
        Digit b = Digit.getRandomExcept(Collections.singleton(a));
        Digit c = findC(a, b);
        Digit d = Digit.getRandomExcept(Collections.singleton(a));
        List<Operation> operations = List.of(Operation.ADDITION, Operation.MULTIPLICATION, Operation.MULTIPLICATION, Operation.DIVISION, Operation.MULTIPLICATION);
        List<Digit> digits = List.of(a, b, c, a, d);
        List<Part> parts = new ArrayList<>();
        for (int i = 0; i < operations.size(); i++) {
            Part part = new Part(operations.get(i), digits.get(i));
            parts.add(part);
        }
        return parts;
    }

    private static Digit findC(Digit a, Digit b) {
        while (true) {
            Digit c = Digit.getRandomExcept(Arrays.asList(a, b));
            if (c.getAsInt() % a.getAsInt() != 0) return c;
        }
    }

    private static Riddle create(int... ints) {
        while (true) {
            Riddle[] riddles = new Riddle[ints.length];
            for (int j = 0; j < ints.length; j++) {
                int i = ints[j];
                riddles[j] = createRiddle(i);
            }
            Riddle c = Riddle.merge(riddles);
            if (isValid(c)) {
                return c;
            }
        }
    }

    private static Riddle createRiddle(int length) {
        Generator generator = new Generator(length);
        Riddle riddle;
        while (true) {
            try {
                riddle = generator.generate();
                if (isValid(riddle)) break;
            } catch (Exception ignored) {

            }
        }
        return riddle;
    }

}
package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.model.Riddle;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;

import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
/*        Part[] parts = new Part[]{
                new Part(Operation.NONE, Digit.FOUR),
                new Part(Operation.NONE, Digit.THREE),
                new Part(Operation.NONE, Digit.TWO),
                new Part(Operation.NONE, Digit.SIX),
                new Part(Operation.NONE, Digit.THREE),
                new Part(Operation.NONE, Digit.NINE),
                new Part(Operation.NONE, Digit.SEVEN),
                new Part(Operation.NONE, Digit.EIGHT),
                new Part(Operation.NONE, Digit.TWO),
                new Part(Operation.NONE, Digit.NINE),
                new Part(Operation.NONE, Digit.FOUR),
                new Part(Operation.NONE, Digit.FOUR),
                new Part(Operation.NONE, Digit.SIX),
                new Part(Operation.NONE, Digit.FOUR),
                new Part(Operation.NONE, Digit.FOUR),
                new Part(Operation.NONE, Digit.FIVE),
        };
        Riddle riddle = new Riddle(parts, 4792);
        Solver solver = new Solver();
        Set<List<Operation>> operations = solver.solve(riddle);
        for (List<Operation> operation : operations) {
            System.out.println(operation);
        }*/
        test(4, 1000);
    }

    private static void test(int length, int amount) {

        Generator generator = new Generator(length);
        Solver solver = new Solver();
        int valid = 0;
        int invalid = 0;
        int zero = 0;
        for (int i = 0; i < amount; i++) {
            // System.out.println("hi");
            Riddle riddle = generator.generate();
            // System.out.println("im generated");
            Set<List<Operation>> solutions = solver.solve(riddle);
            // System.out.println("im solved");
            if (solutions.size() == 0) {
                System.out.println("zero solutions for" + riddle);
                zero++;
                continue;
            }
            if (solutions.size() == 1) {
                valid++;
                continue;
            }
            invalid++;
            System.out.println("Multiple Solutions for " + riddle);
            for (List<Operation> solution : solutions) {
                System.out.println(solution);
            }
        }

        System.out.println(amount + " : valid " + valid + " / invalid " + invalid + " / zero " + zero);
    }
}

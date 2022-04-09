package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.model.Digit;
import com.franosch.bwinf.rechenraetsel.model.Part;
import com.franosch.bwinf.rechenraetsel.model.Riddle;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Solver {

    private final Calculator calculator;

    public Solver() {
        calculator = new Calculator();
    }

    public Set<Operation[]> solve(Riddle riddle) {
        int outcome = riddle.outcome();
        Set<Operation[]> solutions = new HashSet<>();
        Operation[] operations = new Operation[riddle.parts().length];
        operations[0] = Operation.ADDITION;
        solve(Arrays.stream(riddle.parts()).map(Part::digit).toArray(Digit[]::new), 1,
                outcome, operations, solutions);
        return solutions;
    }

    private void solve(final Digit[] digits, int iterationDepth, final int outcome, Operation[] operations, final Set<Operation[]> results) {
        // System.out.println(operations);
        if (digits.length == iterationDepth) {
            Simplification[] simplifications = new Simplification[iterationDepth];
            for (int i = 0; i < simplifications.length; i++) {
                simplifications[i] = new Simplification(operations[i], digits[i].getAsInt());
            }
            double applied = calculator.calculate(simplifications);
            if (applied == outcome) {
                // System.out.println("valid" + Arrays.toString(sol));
                results.add(operations);
            }
            return;
        }
        iterationDepth++;
        // System.out.println(Arrays.toString(sol));
        // System.out.println("parts " + Arrays.toString(parts));
        for (Operation operation : Operation.getValues()) {
            // System.out.println(part);
            // System.out.println(iterationDepth);

            Operation[] copy = operations.clone();
            copy[iterationDepth - 1] = operation;
            if (operation == Operation.DIVISION) {
                try {
                    Simplification[] simplifications = new Simplification[iterationDepth];
                    for (int i = 0; i < simplifications.length; i++) {
                        simplifications[i] = new Simplification(copy[i], digits[i].getAsInt());
                    }
                    calculator.calculate(true, simplifications);
                } catch (ArithmeticException e) {
                    continue;
                }
            }
            solve(digits, iterationDepth, outcome, copy, results);
        }
    }

}

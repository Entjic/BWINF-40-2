package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.model.Part;
import com.franosch.bwinf.rechenraetsel.model.Riddle;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;

import java.util.*;

public class Solver {

    private final Calculator calculator;

    public Solver() {
        calculator = new Calculator();
    }

    public Set<List<Operation>> solve(Riddle riddle) {
        int outcome = riddle.outcome();
        Set<List<Operation>> solutions = new HashSet<>();
        solve(riddle.parts(), 0, outcome, new ArrayList<>(), solutions);
        return solutions;
    }

    private void solve(final Part[] parts, int iterationDepth, final int outcome, List<Operation> operations, final Set<List<Operation>> results) {
        // System.out.println(operations);
        if (parts.length == iterationDepth) {
            Part[] sol = new Part[operations.size()];
            for (int i = 0; i < sol.length; i++) {
                sol[i] = new Part(operations.get(i), parts[i].digit());
            }
            double applied = apply(sol);
            if (applied == outcome) {
                // System.out.println("valid" + Arrays.toString(sol));
                results.add(operations);
            }
            return;
        }
        if (iterationDepth == 0) {
            operations.add(Operation.ADDITION);
            iterationDepth++;
            solve(parts, iterationDepth, outcome, operations, results);
            return;
        }
        Part[] sol = new Part[iterationDepth + 1];
        for (int i = 0; i < iterationDepth; i++) {
            sol[i] = new Part(operations.get(i), parts[i].digit());
        }
        iterationDepth++;
        // System.out.println(Arrays.toString(sol));
        // System.out.println("parts " + Arrays.toString(parts));
        for (Operation operation : Operation.getValues()) {
            Part part = new Part(operation, parts[iterationDepth - 1].digit());
            // System.out.println(part);
            // System.out.println(iterationDepth);
            sol[iterationDepth - 1] = part;
            if (operation.equals(Operation.DIVISION)) {
                try {
                    apply(sol);
                } catch (ArithmeticException e) {
                    continue;
                }
            }
            List<Operation> copy = new ArrayList<>(operations);
            copy.add(operation);
            solve(parts, iterationDepth, outcome, copy, results);
        }
    }


    private double apply(Part[] parts) {
        return calculator.calculate(true, Arrays.stream(parts).map(Simplification::convert).toArray(Simplification[]::new));
    }

}

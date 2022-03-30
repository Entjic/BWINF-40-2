package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.model.Part;
import com.franosch.bwinf.rechenraetsel.model.Riddle;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;

import java.util.*;

public class Solver {

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
            int applied = apply(sol);
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
        for (int i = 0; i < sol.length - 1; i++) {
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
            try {
                apply(sol);
            } catch (ArithmeticException e) {
                continue;
            }
            List<Operation> copy = new ArrayList<>(operations);
            copy.add(operation);
            solve(parts, iterationDepth, outcome, copy, results);
        }
    }


    private int apply(Part[] parts) {
        // System.out.println("parts: " + Arrays.toString(parts));
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
        // System.out.println("out: " + out);
        int applied = 0;
        for (Simplification simplification : out) {
            applied = simplification.operation().apply(applied, simplification.value());
        }
        // System.out.println("applied " + applied);
        return applied;
    }

}

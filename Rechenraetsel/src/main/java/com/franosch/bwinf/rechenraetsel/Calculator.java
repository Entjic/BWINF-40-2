package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Calculator {
    public double calculate(Simplification... simplifications) {
        // System.out.println("parts: " + Arrays.toString(simplifications));
        Stack<Simplification> stack = new Stack<>();
        for (int i = simplifications.length - 1; i >= 0; i--) {
            Simplification simplification = simplifications[i];
            stack.push(simplification);
        }

        List<Simplification> out = new ArrayList<>();
        // System.out.println(stack);
        while (!stack.empty()) {
            Simplification simplification = stack.pop();
            if (stack.isEmpty()) {
                out.add(simplification);
                break;
            }
            Simplification next = stack.peek();
            // System.out.println("current " + stack);
            // System.out.println("next " + next);
            if (next.operation().equals(Operation.MULTIPLICATION) || next.operation().equals(Operation.DIVISION)) {
                stack.pop();
                Simplification result = new Simplification(simplification.operation(), next.operation().apply((int) simplification.value(), (int) next.value()));
                // System.out.println("adding " + result);
                stack.add(result);
                continue;
            }
            out.add(simplification);
        }
        // System.out.println("out: " + out);
        int applied = 0;
        for (Simplification simplification : out) {
            applied = simplification.operation().apply(applied, (int) simplification.value());
        }
        // System.out.println("applied " + applied);
        return applied;
    }
}

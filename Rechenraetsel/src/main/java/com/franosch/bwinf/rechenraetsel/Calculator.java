package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Calculator {
    public double calculate(Simplification... simplifications) {
        return calculate(true, simplifications);
    }

    public double calculate(boolean throwOnError, Simplification... simplifications) {
        // System.out.println("parts: " + Arrays.toString(simplifications));
        // System.out.println("out: " + out);
        int applied = 0;
        for (Simplification simplification : reduce(throwOnError, simplifications)) {
            applied = simplification.operation().apply(applied, (int) simplification.value());
        }
        // System.out.println("applied " + applied);
        return applied;
    }

    public Simplification[] reduce(boolean throwOnError, Simplification... simplifications) {
        if (simplifications.length == 0) return new Simplification[0];
        if (simplifications.length == 1) return new Simplification[]{simplifications[0]};
        Stack<Simplification> stack = new Stack<>();
        for (int i = simplifications.length - 1; i >= 0; i--) {
            Simplification simplification = simplifications[i];
            stack.push(simplification);
        }

        List<Simplification> out = new ArrayList<>();
        while (!stack.empty()) {
            Simplification simplification = stack.pop();
            if (stack.isEmpty()) {
                out.add(simplification);
                break;
            }
            Simplification next = stack.peek();
            // System.out.println("current " + stack);
            // System.out.println("next " + next);
            if (next.operation() == Operation.MULTIPLICATION || next.operation() == Operation.DIVISION) {
                stack.pop();
                Simplification result;
                if (simplification.operation() == Operation.DIVISION) {
                    result = new Simplification(simplification.operation(), Operation.MULTIPLICATION.apply((int) simplification.value(), (int) next.value(), throwOnError));
                } else {
                    result = new Simplification(simplification.operation(), next.operation().apply((int) simplification.value(), (int) next.value(), throwOnError));
                }                // System.out.println("adding " + result);
                stack.add(result);
                continue;
            }
            out.add(simplification);
        }
        return out.toArray(new Simplification[0]);
    }
}

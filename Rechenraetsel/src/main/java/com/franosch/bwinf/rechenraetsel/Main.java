package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.model.Riddle;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;

import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Generator generator = new Generator(6);
        Solver solver = new Solver();
        final int sampleSize = 100;
        int counter = 0;
        for (int i = 0; i < sampleSize; i++) {
            // System.out.println("hi");
            Riddle riddle = generator.generate();
            // System.out.println("im generated");
            Set<List<Operation>> solutions = solver.solve(riddle);
            // System.out.println("im solved");
            if (solutions.size() == 1) {
                continue;
            }
            counter++;
            System.out.println("Multiple Solutions for " + riddle);
            for (List<Operation> solution : solutions) {
                System.out.println(solution);
            }
        }

        System.out.println(sampleSize + " : valid " + (sampleSize - counter) + " / invalid " + counter);

    }
}

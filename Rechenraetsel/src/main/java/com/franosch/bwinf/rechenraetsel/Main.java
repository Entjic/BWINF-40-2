package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.model.Riddle;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import lombok.SneakyThrows;

import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {
    @SneakyThrows
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
        long a = System.currentTimeMillis();
        test(4, 10000);
        long b = System.currentTimeMillis();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(b - a);
        System.out.println(seconds);
/*        FileReader fileReader = new com.franosch.bwinf.rechenraetsel.io.FileReader("outcome", "rechenraetsel/src/main/resources/");
        List<String> strings = new ArrayList<>(fileReader.getContent());
        strings.sort(String::compareTo);
        FileWriter fileWriter = new FileWriter("rechenraetsel/src/main/resources/sorted.txt");
        for (String string : strings) {
            fileWriter.write(string + "\n");
        }
        fileWriter.close();*/
    }

    @SneakyThrows
    private static void test(int length, int amount) {
        FileWriter fileWriter = new FileWriter("rechenraetsel/src/main/resources/outcome.txt");
        Map<String, Integer> map = new HashMap<>();
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
                // System.out.println("valid " + riddle);
                valid++;
                continue;
            }
            invalid++;
            int a = map.getOrDefault(getEquationRepresentation(solutions), 0);
            a++;
            map.put(getEquationRepresentation(solutions), a);
            // System.out.print("Multiple Solutions for " + riddle + " -> ");
            // System.out.println(getEquationRepresentation(solutions));
        }
        System.out.println("write file");
        List<String> strings = new ArrayList<>();
        for (String key : map.keySet()) {
            strings.add(map.get(key) + " -> " + key + "\n");
        }
        strings.sort(String::compareTo);
        for (String string : strings) {
            fileWriter.write(string);
        }
        fileWriter.close();
        System.out.println(amount + " : valid " + valid + " / invalid " + invalid + " / zero " + zero);
    }

    private static String getEquationRepresentation(Set<List<Operation>> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (List<Operation> operations : list) {
            stringBuilder.append(getEquationString(operations));
            stringBuilder.append("=");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    private static String getEquationString(List<Operation> operations) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int i = 0;
        for (Operation operation : operations) {
            switch (operation) {
                case ADDITION, NONE -> stringBuilder.append("+");
                case SUBTRACTION -> stringBuilder.append("-");
                case MULTIPLICATION -> stringBuilder.append("*");
                case DIVISION -> stringBuilder.append(":");
            }
            stringBuilder.append(chars[i]);
            i++;
        }
        return stringBuilder.toString();
    }
}

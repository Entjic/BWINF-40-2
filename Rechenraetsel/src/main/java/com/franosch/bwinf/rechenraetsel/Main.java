package com.franosch.bwinf.rechenraetsel;

import com.franosch.bwinf.rechenraetsel.check.SubSumChecker;
import com.franosch.bwinf.rechenraetsel.logging.LogFormatter;
import com.franosch.bwinf.rechenraetsel.model.Part;
import com.franosch.bwinf.rechenraetsel.model.Riddle;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;
import lombok.SneakyThrows;

import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {


    @SneakyThrows
    public static void main(String[] args) {
        setUpLogger(Level.OFF);
        long sum = 0;
        int amount = 10;
        for (int i = 0; i < amount; i++) {
            long a = System.currentTimeMillis();
            Riddle riddle = createMaster(12);
            long b = System.currentTimeMillis();
            long seconds = b - a;
            System.out.println("riddle: " + riddle.obfuscated());
            System.out.println("solution: " + riddle);
            sum += seconds;
        }
        System.out.println("average time per riddle " + (double) TimeUnit.MILLISECONDS.toSeconds(sum) / amount + "s");

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

    private static Riddle createMaster(int length) {
        Generator generator = new Generator(length);
        switch (length) {
            case 1, 2, 3 -> {
                return generator.generate();
            }
            case 4, 5 -> {
                while (true) {
                    Riddle riddle = generator.generate();
                    if (isValid(riddle)) return riddle;
                }
            }
            case 6 -> {
                return create(3, 3);
            }
            case 7 -> {
                return create(3, 4);
            }
            case 8 -> {
                return create(5, 3);
            }
            case 9 -> {
                return create(4, 5);
            }
            case 10 -> {
                return create(5, 5);
            }
            case 11 -> {
                return create(3, 3, 5);
            }
            case 12 -> {
                return create(5, 4, 3);
            }
            case 13 -> {
                return create(3, 3, 3, 4);
            }
            case 14 -> {
                return create(3, 3, 4, 4);
            }
            case 15 -> {
                return create(3, 3, 3, 3, 3);
            }

        }
        return new Riddle(new Part[0], 0);
    }

    private static Riddle create(int... ints) {
        int counter = 0;
        while (true) {
            Riddle[] riddles = new Riddle[ints.length];
            for (int j = 0; j < ints.length; j++) {
                int i = ints[j];
                riddles[j] = createRiddle(i);
            }
            Riddle c = Riddle.merge(riddles);
            if (isValid(c)) {
                System.out.println(counter);
                return c;
            }
            counter++;
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

    @SneakyThrows
    private static void test(int length, int amount) {
        FileWriter fileWriter = new FileWriter("rechenraetsel/src/main/resources/outcome.txt");
        Map<String, Integer> map = new HashMap<>();
        Generator generator = new Generator(length);
        Solver solver = new Solver();
        List<String> strings = new ArrayList<>();
        int valid = 0;
        int invalid = 0;
        int error = 0;
        for (int i = 0; i < amount; i++) {
            // System.out.println("hi");
            try {
                Riddle riddle = generator.generate();
                // System.out.println("im generated");
                Set<Operation[]> solutions = solver.solve(riddle);
                // System.out.println("im solved");
                if (solutions.size() == 1) {
                    // System.out.println("valid " + riddle);
                    valid++;
                    continue;
                }
                invalid++;/*
                int a = map.getOrDefault(getEquationRepresentation(solutions), 0);
                a++;
                map.put(getEquationRepresentation(solutions), a);*/
                strings.add(riddle + " " + getEquationRepresentation(solutions));
            } catch (Exception e) {
                error++;
                continue;
            }
        }
        System.out.println("write file");
/*        for (String key : map.keySet()) {
            strings.add(map.get(key) + " -> " + key + "\n");
        }*/
        strings.sort(String::compareTo);
        for (String string : strings) {
            fileWriter.write(string + "\n ");
        }
        fileWriter.close();
        System.out.println(amount + " : valid " + valid + " / invalid " + invalid + " / error " + error);
    }

    private static String getEquationRepresentation(Set<Operation[]> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Operation[] operations : list) {
            stringBuilder.append(getEquationString(operations));
            stringBuilder.append("=");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    private static String getEquationString(Operation[] operations) {
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

    private static void setUpLogger(Level level) {
        Logger.getGlobal().setLevel(level);
        Logger.getGlobal().setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new LogFormatter());
        Logger.getGlobal().addHandler(consoleHandler);
    }
}

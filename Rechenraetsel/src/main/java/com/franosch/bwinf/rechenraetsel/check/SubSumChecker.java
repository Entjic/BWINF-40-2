package com.franosch.bwinf.rechenraetsel.check;

import com.franosch.bwinf.rechenraetsel.Calculator;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SubSumChecker {
    private final Calculator calculator;
    private final Logger logger = Logger.getGlobal();

    public SubSumChecker() {
        this.calculator = new Calculator();
    }

    public boolean isSubSum(Simplification... simplifications) {
        // System.out.println("simplification" + Arrays.toString(simplifications));
        if (simplifications.length < 2) return false;
        List<Simplification> simplificationList = Arrays.stream(simplifications).toList();
        Set<Simplification[]> subSets = generateSubSets(simplifications).stream()
                .map(set -> Arrays.stream(set).filter(Objects::nonNull)
                        .toArray(Simplification[]::new)).filter(set -> set.length != 0).collect(Collectors.toSet());
        for (Simplification[] subSet : subSets) {
            List<Simplification> copy = new ArrayList<>(simplificationList);
            for (Simplification simplification : subSet) {
                copy.remove(simplification);
            }
            Set<Simplification[]> complementarySubSets = generateSubSets(copy.toArray(new Simplification[0])).stream()
                    .map(set -> Arrays.stream(set).filter(Objects::nonNull)
                            .toArray(Simplification[]::new)).filter(set -> set.length != 0).collect(Collectors.toSet());
            for (Simplification[] complementarySubSet : complementarySubSets) {
                double appliedA = calculator.calculate(false, subSet);
                double appliedB = calculator.calculate(false, complementarySubSet);
                if (appliedA != Math.floor(appliedA)) continue;
                if (appliedA * -1 == appliedB) { // FIXME: 03.04.2022 das könnte ein problem sein
                    logger.info("a" + Arrays.toString(subSet) + " -> " + appliedA);
                    logger.info("b" + Arrays.toString(complementarySubSet) + " -> " + appliedB);
                    // System.out.println("failed same sub sum test");
                    return true;
                }
            }
        }
        return false;
    }

    private Set<Simplification[]> generateSubSets(Simplification[] simplifications) {
        Set<Simplification[]> subSets = new HashSet<>();
        generateSubSet(simplifications, new Simplification[simplifications.length], 0, 0, subSets);
        return subSets;
    }

    private void generateSubSet(Simplification[] origin, Simplification[] subSet, int pos, int iterationDepth, Set<Simplification[]> out) {
        if (pos == origin.length) {
            out.add(subSet);
            return;
        }
        Simplification[] copy = subSet.clone();
        copy[iterationDepth] = origin[pos];
        pos++;
        generateSubSet(origin, subSet, pos, iterationDepth, out);
        iterationDepth++;
        generateSubSet(origin, copy, pos, iterationDepth, out);
    }
}

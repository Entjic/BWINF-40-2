package com.franosch.bwinf.muellabfuhr.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
public class Result implements Comparable<Result> {
    @Getter
    private final Collection<Runner> runners;

    public double getWorstWeight() {
        double weight = 0;
        for (Runner runner : runners) {
            double currentWeight = runner.calcWeight();
            if (currentWeight > weight) {
                weight = currentWeight;
            }
        }
        return weight;
    }


    @Override
    public int compareTo(Result o) {
        double combined = this.getWorstWeight() - o.getWorstWeight();
        return (int) combined;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (Runner runner : runners) {
            stringBuilder.append("\n").append(i).append(" ").append(runner);
            i++;
        }
        stringBuilder.append("\n");
        return "Result{" + stringBuilder + "}";
    }
}

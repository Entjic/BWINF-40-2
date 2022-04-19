package com.franosch.bwinf.zara;

import com.franosch.bwinf.zara.model.DataSet;
import com.franosch.bwinf.zara.model.Mastercard;

import java.util.*;

public class Solver {

    public List<DataSet[]> calcSubsets(DataSet[] superSet, int k) {
        List<DataSet[]> solutions = new ArrayList<>();
        getSubsets(superSet, k, 0, 0, new DataSet[k], solutions);
        return solutions;
    }

    private void getSubsets(DataSet[] superSet, int k, int index, int currentIndex, DataSet[] current, List<DataSet[]> solution) {
        if (currentIndex == k) {
            solution.add(current);
            return;
        }
        if (index == superSet.length) return;
        DataSet x = superSet[index];
        DataSet[] copy = Arrays.copyOf(current, k);
        copy[currentIndex] = x;
        getSubsets(superSet, k, index + 1, currentIndex, current, solution);
        getSubsets(superSet, k, index + 1, currentIndex + 1, copy, solution);
    }

    public List<DataSet> sort(Collection<DataSet> input, int i) {
        List<DataSet> list = new ArrayList<>();
        for (DataSet dataSet : input) {
            if (!dataSet.getContent()[i]) {
                list.add(0, dataSet);
                continue;
            }
            list.add(dataSet);
        }
        return list;
    }

    public void solve(Collection<DataSet> dataSets, int recursionDepth, int maxRecursionDepth, int length, Set<DataSet> result, List<DataSet> chosen) {
/*        System.out.println(dataSets.size());
        for (DataSet dataSet : dataSets) {
            System.out.println(dataSet);
        }
        System.out.println("chosen");
        for (DataSet dataSet : chosen) {
            System.out.println(dataSet);
        }*/
        if (dataSets.size() < 1) {
            return;
        }
        if (recursionDepth == maxRecursionDepth) {
            if (isResult(dataSets, chosen, length)) {
                result.addAll(dataSets);
            }
            return;
        }

        List<DataSet> sorted = sort(dataSets, recursionDepth);
        List<DataSet> zero = getZeros(sorted, recursionDepth);
        List<DataSet> one = new ArrayList<>(sorted);
        one.removeAll(zero);

        List<DataSet[]> subSet = calcSubsets(zero.toArray(new DataSet[0]), 2);
        for (DataSet[] sets : subSet) {
            List<DataSet> copy = new ArrayList<>(zero);
            for (DataSet dataSet : sets) {
                copy.remove(dataSet);
            }
            List<DataSet> currentChosen = new ArrayList<>(chosen);
            currentChosen.addAll(List.of(sets));
            solve(copy, recursionDepth + 1, maxRecursionDepth, length, result, currentChosen);
        }
        for (int i = 2; i < length; i = i + 2) { // FIXME: 18.04.2022 Das müsste in der gleichen for behandelt werden können
            List<DataSet[]> oneSets = calcSubsets(one.toArray(new DataSet[0]), i);
            for (DataSet[] oneSet : oneSets) {
                List<DataSet> currentChosen = new ArrayList<>(chosen);
                currentChosen.addAll(List.of(oneSet));
                solve(zero, recursionDepth + 1, maxRecursionDepth, length, result, currentChosen);
            }
        }
    }

    private List<DataSet> getZeros(List<DataSet> dataSets, int recursionDepth) {
        List<DataSet> zero = new ArrayList<>();
        for (DataSet dataSet : dataSets) {
            if (!dataSet.getContent()[recursionDepth]) {
                zero.add(dataSet);
            }
        }
        return zero;
    }

    private boolean isResult(Collection<DataSet> assumption, Collection<DataSet> chosen, int length) {
        Mastercard mastercard = new Mastercard(chosen.toArray(DataSet[]::new));
        Collection<DataSet[]> subSets = calcSubsets(assumption.toArray(new DataSet[0]), length - chosen.size());
        for (DataSet[] subSet : subSets) {
            Mastercard m = new Mastercard(subSet);
            if (isEquals(mastercard, m)) {
                System.out.println("SOLUTION");
                for (DataSet dataSet : subSet) {
                    System.out.println(dataSet);
                }
                for (DataSet dataSet : chosen) {
                    System.out.println(dataSet);
                }
                System.exit(0);
                return true;
            }
        }
        return false;
    }

    private boolean isEquals(Mastercard a, Mastercard b) {
        boolean[] dataSetA = a.getContent().getContent();
        boolean[] dataSetB = b.getContent().getContent();
        for (int i = 0; i < DataSet.keyLength; i++) {
            if (dataSetA[i] != dataSetB[i]) return false;
        }
        return true;
    }


}

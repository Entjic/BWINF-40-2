package com.franosch.bwinf.zara;

import com.franosch.bwinf.zara.model.DataSet;
import com.franosch.bwinf.zara.model.Mastercard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Solver {

    private long time;

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

    public Set<DataSet> solve(Collection<DataSet> dataSets, int length) {
        for (DataSet dataSet : dataSets) {
            System.out.println(dataSet);
        }
        Set<DataSet> result = new HashSet<>();
        time = System.currentTimeMillis();
        int bitLength = dataSets.stream().findAny().get().getKeyLength();
        System.out.println(length);
        System.out.println(bitLength);
        solve(dataSets, dataSets, 0, length / 2, length + 1, new ArrayList<>(), new DataSet(true, bitLength));
        System.out.println(result);
        return result;
    }

    public void solve(Collection<DataSet> all, Collection<DataSet> dataSets, int recursionDepth, int maxRecursionDepth, int length, List<DataSet> chosen, DataSet bitsUsed) {
//        boolean a = false;
//        boolean b = false;
//        boolean c = false;
//        boolean d = false;
//        for(DataSet x : chosen) {
//            if (x.getId() == 18) a = true;
//            if (x.getId() == 20) b = true;
//            if (x.getId() == 5) c = true;
//            if (x.getId() == 7) d = true;
//        }
//        if(a && b && c && d){
//            System.out.println("here");
//        }
        if (length - chosen.size() <= 5 || dataSets.size() <= 1 || recursionDepth == maxRecursionDepth) {
            solve2(all, chosen);
            return;
        }

        int index = nextBit(bitsUsed, chosen);
        // System.out.println(index);
        DataSet copyBitsUsed = new DataSet(Arrays.copyOf(bitsUsed.getContent(), bitsUsed.getContent().length));
        copyBitsUsed.getContent()[index] = true;
        List<DataSet> sorted = sort(dataSets, index);
        List<DataSet> zero = getZeros(sorted, index);
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
            // System.out.println("current chosen " + Arrays.toString(currentChosen.stream().mapToInt(DataSet::getId).toArray()));
            solve(all, copy, recursionDepth + 1, maxRecursionDepth, length, currentChosen, copyBitsUsed);
            if (length - currentChosen.size() > 5) {
                solve(all, one, recursionDepth + 1, maxRecursionDepth, length, currentChosen, copyBitsUsed);
            }
        }
        List<DataSet[]> oneSets = calcSubsets(one.toArray(new DataSet[0]), 2);
        for (DataSet[] oneSet : oneSets) {
            List<DataSet> currentChosen = new ArrayList<>(chosen);
            currentChosen.addAll(List.of(oneSet));
            // System.out.println("current chosen " + Arrays.toString(currentChosen.stream().mapToInt(DataSet::getId).toArray()));
            List<DataSet> copy = new ArrayList<>(one);
            copy.removeAll(List.of(oneSet));
            solve(all, zero, recursionDepth + 1, maxRecursionDepth, length, currentChosen, copyBitsUsed);
            if (length - chosen.size() > 5) {
                solve(all, copy, recursionDepth + 1, maxRecursionDepth, length, currentChosen, copyBitsUsed);
            }
        }

    }

    private int getNextCandidate(Collection<DataSet> all, int max) {
        int half = max / 2;
        for (int k = 0; k < max; k++) {
            int count = 0;
            for (DataSet dataSet : all) {
                if (!dataSet.getContent()[k]) {
                    count++;
                    if (count > half) {
                        return k;
                    }
                }
            }
        }
        return 0;
    }


    private int nextBit(DataSet bitsUsed, List<DataSet> chosen) {
        if (chosen.isEmpty()) {
            return getNextCandidate(chosen, bitsUsed.getContent().length);
        }
        DataSet a = chosen.get(chosen.size() - 1);
        DataSet b = chosen.get(chosen.size() - 2);
        Mastercard mastercard = new Mastercard(new DataSet[]{a, b});
        int i = 0;
        for (boolean bool : mastercard.getContent().getContent()) {
            if (!bool && !bitsUsed.getContent()[i]) return i;
            i++;
        }
        throw new RuntimeException("No more bits");
    }

    private List<DataSet> getZeros(List<DataSet> dataSets, int i) {
        List<DataSet> zero = new ArrayList<>();
        for (DataSet dataSet : dataSets) {
            if (!dataSet.getContent()[i]) {
                zero.add(dataSet);
            }
        }
        return zero;
    }

    public void solve2(Collection<DataSet> allDataSets, Collection<DataSet> chosen) {
        int index = 0;
        DataSet xor;

        if (chosen.size() == 0) {
            xor = new DataSet(true, allDataSets.iterator().next().getKeyLength());
        } else {
            xor = XOR(chosen);
        }
        for (boolean b : xor.getContent()) {
            if (!b) break;
            index++;
        }
        List<DataSet> sorted = sort(allDataSets, index);
        List<DataSet> zero = getZeros(sorted, index);
        List<DataSet> one = new ArrayList<>(sorted);
        one.removeAll(zero);
//        List<DataSet> filtered = new ArrayList<>();
//        boolean[] content = xor.getContent();
//        for (DataSet dataSet : zero) {
//            boolean b = false;
//            for (int j = 0; j < content.length; j++) {
//                if (!content[j]) {
//                    if (dataSet.getContent()[j]) {
//                        b = true;
//                        break;
//                    }
//                }
//            }
//            if (!b) {
//                filtered.add(dataSet);
//            }
//        }
//        StringBuilder sb = new StringBuilder();
//        sb.append("chosen ");
//        for(DataSet s : chosen){
//            sb.append(s.getId()).append(" ");
//        }
//        System.out.println("chosen: "  + sb.toString());
        if (zero.isEmpty()) return;
        //  System.out.println("filtered " + zero.size() + " one " + one.size());
        List<DataSet[]> fiveZeroSets = calcSubsets(zero.toArray(new DataSet[0]), 5);

        boolean[] s0, s1, s2, s3, s4;
        boolean[] chosenXORArray = xor.getContent();
        boolean found = false;
        int length = chosenXORArray.length;
        for (DataSet[] sets : fiveZeroSets) {
            s0 = sets[0].getContent();
            s1 = sets[1].getContent();
            s2 = sets[2].getContent();
            s3 = sets[3].getContent();
            s4 = sets[4].getContent();

            for (int i = 0; i < length; i++) {
                if (s0[i] ^ s1[i] ^ s2[i] ^ s3[i] ^ s4[i] ^ chosenXORArray[i]) {
                    break;
                }
                if (i == length - 1) {
                    found = true;
                }
            }
            if (found) {
                printSolution(sets, chosen);
                System.exit(0);
            }
        }
//            Collection<DataSet> collection = new ArrayList<>(List.of(sets));
//            collection.add(chosenXOR);
//            DataSet current = XOR(collection);
//            if (isResult(current)) {
//                printSolution(sets, chosen);
//                System.exit(0);
//            }

        List<DataSet[]> threeZeroSets = calcSubsets(zero.toArray(new DataSet[0]), 3);
        List<DataSet[]> oneSets = calcSubsets(one.toArray(new DataSet[0]), 2);
        for (DataSet[] oneSet : oneSets) {
//            DataSet xorSetOne = XOR(oneSet);
//            DataSet xorSetOneChosen = XOR(xorSetOne, chosenXOR);
//            for (int j = 0, threeZeroSetsSize = threeZeroSets.size(); j < threeZeroSetsSize; j++) {
//                DataSet[] threeZeroSet = threeZeroSets.get(j);
//                DataSet xorThree = XOR(threeZeroSet);
//                DataSet combined = XOR(xorSetOneChosen, xorThree);
//                if (isResult(combined)) {
//                    System.out.println("lösungsausgabe kaputt");
//                    printSolution(oneSet, chosen);
//                    System.exit(0);
//
//                }
//            }
            s0 = oneSet[0].getContent();
            s1 = oneSet[1].getContent();

            for (DataSet[] oneOutOfThreeZero : threeZeroSets) {
                s2 = oneOutOfThreeZero[0].getContent();
                s3 = oneOutOfThreeZero[1].getContent();
                s4 = oneOutOfThreeZero[2].getContent();
                for (int i = 0; i < length; i++) {
                    if (s0[i] ^ s1[i] ^ s2[i] ^ s3[i] ^ s4[i] ^ chosenXORArray[i]) {
                        break;
                    }
                    if (i == length - 1) {
                        found = true;
                    }

                }
                if (found) {
                    printSolution(oneOutOfThreeZero, chosen);
                    System.out.println("also solution: " + oneSet);
                    System.exit(0);
                }
            }

        }

        List<DataSet[]> fourOneSets = calcSubsets(one.toArray(new DataSet[0]), 4);
        for (DataSet[] oneFourSet : fourOneSets) {
            s0 = oneFourSet[0].getContent();
            s1 = oneFourSet[1].getContent();
            s2 = oneFourSet[2].getContent();
            s3 = oneFourSet[3].getContent();
//            DataSet sdfdfhsdfg = XOR(sets);
//            DataSet dvbnsdergcfg = XOR(sdfdfhsdfg, chosenXOR);
//
//            for (DataSet dataSet : zero) {
//                DataSet xcvbdf = XOR(dvbnsdergcfg, dataSet);
//                if (isResult(xcvbdf)) {
//                    chosen.add(dataSet);
//                    printSolution(sets, chosen);
//                    System.exit(0);
//                }
//            }
            for (DataSet oneZeroSet : zero) {
                s4 = oneZeroSet.getContent();
                for (int i = 0; i < length; i++) {
                    if (s0[i] ^ s1[i] ^ s2[i] ^ s3[i] ^ s4[i] ^ chosenXORArray[i]) {
                        break;
                    }
                    if (i == length - 1) {
                        found = true;
                    }
                }
                if (found) {
                    printSolution(oneFourSet, chosen);
                    System.out.println("also solution: " + oneZeroSet);
                    System.exit(0);
                }
            }
        }

    }


    private boolean isResult(DataSet dataSet) {
        boolean[] content = dataSet.getContent();
        for (int i = 0, contentLength = content.length; i < contentLength; i++) {
            if (content[i]) return false;
        }
        return true;
    }

    private boolean isResult(Mastercard combined, DataSet zero) {
        return isEquals(new Mastercard(new DataSet[]{zero}), combined);
    }


    private void printSolution(DataSet[] subSet, Collection<DataSet> chosen) {

        System.out.println("SOLUTION");
        for (DataSet dataSet : subSet) {
            System.out.println(dataSet);
        }
        for (DataSet dataSet : chosen) {
            System.out.println(dataSet);
        }
        long dif = System.currentTimeMillis() - time;
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        System.out.println("required time " + dateFormat.format(dif));
    }

    private boolean isEquals(Mastercard a, Mastercard b) {
        boolean[] dataSetA = a.getContent().getContent();
        boolean[] dataSetB = b.getContent().getContent();
        for (int i = 0; i < dataSetA.length; i++) {
            if (dataSetA[i] != dataSetB[i]) return false;
        }
        return true;
    }

    private boolean isEquals(DataSet a, DataSet b) {
        boolean[] contentA = a.getContent();
        boolean[] contentB = b.getContent();
        for (int i = 0, contentLength = contentA.length; i < contentLength; i++) {
            if (contentA[i] != contentB[i]) return false;
        }
        return true;
    }

    private DataSet XOR(DataSet... dataSets) {
        DataSet current = null;
        for (DataSet dataSet : dataSets) {
            if (current == null) {
                current = dataSet;
                continue;
            }
            current = XOR(current, dataSet);
        }
        return current;
    }

    private DataSet XOR(Collection<DataSet> dataSets) {
        DataSet current = null;
        for (DataSet dataSet : dataSets) {
            if (current == null) {
                current = dataSet;
                continue;
            }
            current = XOR(current, dataSet);
        }
        return current;
    }

    private DataSet XOR(DataSet a, DataSet b) {
        return new DataSet(XOR(a.getContent(), b.getContent()));
    }

    private boolean[] XOR(boolean[] a, boolean[] b) {
        boolean[] booleans = new boolean[a.length];
        for (int i = 0; i < booleans.length; i++) {
            booleans[i] = a[i] ^ b[i];
        }
        return booleans;
    }


}

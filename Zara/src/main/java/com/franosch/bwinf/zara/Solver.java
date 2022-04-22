package com.franosch.bwinf.zara;

import com.franosch.bwinf.zara.model.DataSet;
import com.franosch.bwinf.zara.model.Mastercard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

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
        solve(dataSets, dataSets, 0, 2, length + 1, new ArrayList<>(), new DataSet(true, bitLength));
        System.out.println(result);
        return result;
    }

    public void solve(Collection<DataSet> all, Collection<DataSet> dataSets, int recursionDepth, int maxRecursionDepth, int length, List<DataSet> chosen, DataSet bitsUsed) {

        if (length - chosen.size() <= 5 || dataSets.size() <= 1 || recursionDepth == maxRecursionDepth) {
            solve3(all, chosen);
            return;
        }
        int[] zeroCounted = countBits(all); // TODO: 21.04.2022
        int index = nextBit(bitsUsed, chosen);
        // System.out.println(index);
        DataSet copyBitsUsed = new DataSet(Arrays.copyOf(bitsUsed.getContent(), bitsUsed.getContent().length));
        copyBitsUsed.getContent()[index] = true;
        List<DataSet> sorted = sort(dataSets, index);
        List<DataSet> zero = getZeros(sorted, index);
        List<DataSet> one = new ArrayList<>(sorted);
        one.removeAll(zero);

        List<DataSet[]> zeroSubSets = calcSubsets(zero.toArray(new DataSet[0]), 2);
        if (recursionDepth == 0) {
            new Thread(() -> {
                System.out.println("thread 1 alive");
                for (DataSet[] sets : zeroSubSets) {
                    List<DataSet> copy = new ArrayList<>(zero);
                    for (DataSet dataSet : sets) {
                        copy.remove(dataSet);
                    }
                    List<DataSet> currentChosen = new ArrayList<>(chosen);
                    currentChosen.addAll(List.of(sets));
                    // System.out.println("current chosen " + Arrays.toString(currentChosen.stream().mapToInt(DataSet::getId).toArray()));
                    solve(all, copy, recursionDepth + 1, maxRecursionDepth, length, currentChosen, copyBitsUsed);
                }
            }).start();
            new Thread(() -> {
                System.out.println("thread 2 alive");
                List<DataSet[]> oneSets = calcSubsets(one.toArray(new DataSet[0]), 2);
                for (DataSet[] oneSet : oneSets) {
                    List<DataSet> currentChosen = new ArrayList<>(chosen);
                    currentChosen.addAll(List.of(oneSet));
                    // System.out.println("current chosen " + Arrays.toString(currentChosen.stream().mapToInt(DataSet::getId).toArray()));

                    solve(all, zero, recursionDepth + 1, maxRecursionDepth, length, currentChosen, copyBitsUsed);

                }
            }).start();
            return;
        }

        for (DataSet[] sets : zeroSubSets) {
            List<DataSet> copy = new ArrayList<>(zero);
            for (DataSet dataSet : sets) {
                copy.remove(dataSet);
            }
            List<DataSet> currentChosen = new ArrayList<>(chosen);
            currentChosen.addAll(List.of(sets));
            // System.out.println("current chosen " + Arrays.toString(currentChosen.stream().mapToInt(DataSet::getId).toArray()));
            solve(all, copy, recursionDepth + 1, maxRecursionDepth, length, currentChosen, copyBitsUsed);

        }
        List<DataSet[]> oneSets = calcSubsets(one.toArray(new DataSet[0]), 2);
        for (DataSet[] oneSet : oneSets) {
            List<DataSet> currentChosen = new ArrayList<>(chosen);
            currentChosen.addAll(List.of(oneSet));
            // System.out.println("current chosen " + Arrays.toString(currentChosen.stream().mapToInt(DataSet::getId).toArray()));

            solve(all, zero, recursionDepth + 1, maxRecursionDepth, length, currentChosen, copyBitsUsed);

        }

    }

    private int[] countBits(Collection<DataSet> dataSets) {
        int length = dataSets.stream().findAny().get().getKeyLength();
        int[] out = new int[length];
        for (int i = 0; i < length; i++) {
            int counter = 0;
            for (DataSet dataSet : dataSets) {
                if (!dataSet.getContent()[i]) counter++;
            }
            out[i] = counter;
        }
        return out;
    }

    public void solve3(Collection<DataSet> all, Collection<DataSet> chosen) {
        int index = -1;
        DataSet xor;

        if (chosen.size() == 0) {
            xor = new DataSet(true, all.iterator().next().getKeyLength());
        } else {
            xor = XOR(chosen);
        }


        index = getIndex(xor, index);
        List<DataSet> sorted = sort(all, index);
        sorted.removeAll(chosen);


        checkForSolution(chosen, index, xor, sorted);
    }

    private void checkForSolution(Collection<DataSet> chosen, int index, DataSet xor, List<DataSet> dataSets) {
        index = getIndex(xor, index);
        List<DataSet> sorted = sort(dataSets, index);
        List<DataSet> zero = getZeros(dataSets, index);
        List<DataSet> one = new ArrayList<>(sorted);
        one.removeAll(zero);
        for (int i = 0; i < 11 - chosen.size() && 11 - chosen.size() - i >= 0; i += 2) {
            testForSolution(zero, 11 - chosen.size() - i, one, i, xor, chosen, 0, index);
        }
    }

    private void testForSolution(List<DataSet> zero, int zeros, List<DataSet> one, int ones, DataSet xor, Collection<DataSet> chosen, int recursionDepth, int index) {

        if (zero.size() >= zeros && one.size() >= ones) {

            if (recursionDepth <= 1) {
                //     List<DataSet[]> zeroSubSets = calcSubsets(zero.toArray(new DataSet[0]), zeros);

                //  System.out.println("chosen " + chosen.stream().map(x -> x.getId()).collect(Collectors.toList()));
                //List<DataSet[]> oneSubSets = calcSubsets(one.toArray(new DataSet[0]), ones);
                List<DataSet[]> oneSubSets = generateSubsets(chosen, index, xor, one, ones);
                // List<DataSet[]> zeroSubSets = calcSubsets(zero.toArray(new DataSet[0]), zeros);
                List<DataSet[]> zeroSubSets = generateSubsets(chosen, index, xor, zero, zeros);
                checkSolution(zeros, ones, xor, chosen, zeroSubSets, oneSubSets);
                return;
            }
        }
    }

    private List<DataSet[]> generateSubsets(List<DataSet> zero, int zeros, int recursionDepth, int index) {
        List<DataSet[]> zeroSubSets = calcSubsets(zero.toArray(new DataSet[0]), zeros);

        // return calcSubsetsRecursion(zeroSubSets, oneSubSets);
        return zeroSubSets;
    }

    private List<DataSet[]> generateSubsets(Collection<DataSet> chosen, int index, DataSet xor, List<DataSet> dataSets, int num) {
        if (num == 0) {
            return new ArrayList<>();
        }
        if (dataSets.size() < 2 * num || num == 1) {
            return calcSubsets(dataSets.toArray(new DataSet[0]), num);
        }
        index = getIndex(xor, index);
        List<DataSet> sorted = sort(dataSets, index);
        List<DataSet> zero = getZeros(dataSets, index);
        List<DataSet> one = new ArrayList<>(sorted);
        one.removeAll(zero);
        List<DataSet[]> generated = new ArrayList<>();

        for (int i = 0; i < num; i += 2) {
            generated.addAll(generateSubsetsRecursion(chosen, index, xor, zero, num - i, one, i));
        }

        return generated;
    }

    private List<DataSet[]> generateSubsetsRecursion(Collection<DataSet> chosen, int index, DataSet xor, List<DataSet> zero, int numZero, List<DataSet> one, int numOne) {
        List<DataSet[]> zeroSubSets = calcSubsets(zero.toArray(new DataSet[0]), numZero);
        //List<DataSet[]> zeroSubSets = generateSubsets(chosen, index, xor, zero, numZero);
        List<DataSet[]> oneSubSets = calcSubsets(one.toArray(new DataSet[0]), numOne);
        //List<DataSet[]> oneSubSets = generateSubsets(chosen, index, xor, one, numOne);
        List<DataSet[]> combined = calcSubsetsRecursion(zeroSubSets, oneSubSets);
        return combined;
    }


    private List<DataSet[]> calcSubsetsRecursion(List<DataSet[]> zero, List<DataSet[]> one) {
        List<DataSet[]> set = new ArrayList<>();
        int i = 0;
        for (DataSet[] z : zero) {
            for (DataSet[] o : one) {
                DataSet[] s = new DataSet[z.length + o.length];
                i = 0;
                for (DataSet z1 : z) {
                    s[i] = z1;
                    i++;
                }
                for (DataSet o1 : o) {
                    s[i] = o1;
                    i++;
                }

                set.add(s);
            }
        }
        return set;
    }


    private void checkSolution(int zeros, int ones, DataSet xor, Collection<DataSet> chosen, List<DataSet[]> zeroSubSets, List<DataSet[]> oneSubSets) {
        for (DataSet[] dataSets : zeroSubSets) {
            for (DataSet[] sets : oneSubSets) {
                if (isResult(xor, dataSets, sets)) {
                    printSolution(chosen, dataSets, sets);
                    System.out.println(zeros + " " + ones);
                    System.exit(0);
                }
            }
        }
    }


    private boolean isResult(DataSet chosenXOR, DataSet[]... subSets) {
        int length = 0;
        for (DataSet[] subSet : subSets) {
            length += subSet.length;
        }
        boolean[][] booleans = new boolean[length + 1][];
        int i = 0;
        for (DataSet[] subSet : subSets) {
            for (DataSet dataSet : subSet) {
                booleans[i] = dataSet.getContent();
                i++;
            }
        }
        booleans[i] = chosenXOR.getContent();
        return isResult(booleans);
    }

    private boolean isResult(boolean[]... cards) {
        for (int i = 0; i < cards[0].length; i++) {
            boolean c = false;
            for (int j = 0, cardsLength = cards.length; j < cardsLength; j++) {
                boolean[] card = cards[j];
                c = c ^ card[i];
            }
            if (c) return false;
        }
        return true;
    }

    private int getIndex(DataSet xor, int index) {
        boolean[] bools = xor.getContent();
        for (int i = index + 1; i < xor.getKeyLength(); i++) {
            if (!bools[i]) return i;
        }
        return 0;
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
        index = getIndex(xor, index);
        List<DataSet> sorted = sort(allDataSets, index);
        List<DataSet> zero = getZeros(sorted, index);
        List<DataSet> one = new ArrayList<>(sorted);
        one.removeAll(zero);

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

        List<DataSet[]> threeZeroSets = calcSubsets(zero.toArray(new DataSet[0]), 3);
        List<DataSet[]> oneSets = calcSubsets(one.toArray(new DataSet[0]), 2);
        for (DataSet[] oneSet : oneSets) {

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
                    System.out.println("also solution: " + Arrays.toString(oneSet));
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

    private void printSolution(Collection<DataSet> chosen, DataSet[]... set) {
        System.out.println("SOLUTION");
        for (DataSet[] dataSets : set) {
            for (DataSet dataSet : dataSets) {
                System.out.println(dataSet);
            }
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

package com.franosch.bwinf.zara;

import com.franosch.bwinf.zara.model.DataSet;
import com.franosch.bwinf.zara.model.Mastercard;
import lombok.RequiredArgsConstructor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class Solver {
    private final int keys;
    private long time;
    private final List<DataSet[]> empty = new ArrayList<>();

    public List<DataSet[]> calcSubsets(DataSet[] superSet, int k) {
        List<DataSet[]> solutions = new ArrayList<>();
        getSubsets(superSet, k, 0, 0, new DataSet[k], solutions);
        return solutions;
    }


    private <T> void getSubsets(T[] superSet, int k, int index, int currentIndex, T[] current, List<T[]> solution) {
        if (currentIndex == k) {
            solution.add(current);
            return;
        }
        if (index == superSet.length) return;
        T x = superSet[index];
        T[] copy = Arrays.copyOf(current, k);
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

    public Set<DataSet> solve(Collection<DataSet> dataSets) {
        for (DataSet dataSet : dataSets) {
            System.out.println(dataSet);
        }
        Set<DataSet> result = new HashSet<>();
        time = System.currentTimeMillis();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        int bitLength = dataSets.stream().findAny().get().getKeyLength();
        System.out.println(keys);
        System.out.println(bitLength);
        System.out.println(dateFormat.format(time));
        solve(dataSets, dataSets, 0, 2, keys + 1, new ArrayList<>(), new DataSet(true, bitLength));
        System.out.println(result);
        return result;
    }

    public void solve(Collection<DataSet> all, Collection<DataSet> dataSets, int recursionDepth, int maxRecursionDepth, int length, List<DataSet> chosen, DataSet bitsUsed) {

        if (length - chosen.size() <= 5 || dataSets.size() <= 1 || recursionDepth == maxRecursionDepth) {
            solve3(all, chosen);
            return;
        }
        int[] zeroCounted = countBits(all); // TODO: 21.04.2022
        System.out.println(Arrays.toString(zeroCounted));
        int index = nextBit(bitsUsed, chosen);
        // System.out.println(index);
        DataSet copyBitsUsed = new DataSet(Arrays.copyOf(bitsUsed.getContent(), bitsUsed.getContent().length));
        copyBitsUsed.getContent()[index] = true;
        List<DataSet> sorted = sort(dataSets, index);
        List<DataSet> zero = getZeros(sorted, index);
        List<DataSet> one = new ArrayList<>(sorted);
        one.removeAll(zero);
        final int max = (int) (binomial(zero.size(), 4) + binomial(one.size(), 4)) / 10;
        final List<Integer> list = List.of(max, 2 * max, 3 * max, 4 * max, 5 * max, 6 * max, 7 * max, 8 * max, 9 * max, 10 * max);
        System.out.println(list);
        AtomicInteger counter = new AtomicInteger();
        AtomicInteger progress = new AtomicInteger();

        Collection<DataSet[]> zeroFourSubSets = calcSubsets(zero.toArray(DataSet[]::new), 4);
        for (Collection<DataSet[]> sets : split(zeroFourSubSets)) {
            new Thread(() -> {
                for (DataSet[] zeroFourSubSet : sets) {
                    int current = counter.incrementAndGet();
                    if (list.contains(current)) {
                        System.out.println(progress.incrementAndGet() * 10 + "%");
                    }
                    solve3(all, List.of(zeroFourSubSet));
                }
            }).start();
        }

        Collection<DataSet[]> oneFourSubSets = calcSubsets(one.toArray(DataSet[]::new), 4);
        for (Collection<DataSet[]> sets : split(oneFourSubSets)) {
            new Thread(() -> {
                for (DataSet[] oneFourSubSet : sets) {
                    int current = counter.incrementAndGet();
                    if (list.contains(current)) {
                        System.out.println(progress.incrementAndGet() * 10 + "%");
                    }
                    solve3(all, List.of(oneFourSubSet));
                }
            }).start();
        }
    }

    private <T> Collection<Collection<T>> split(Collection<T> splitMe) {
        int a = splitMe.size();
        a = a / 4;
        Collection<T> collection0 = new ArrayList<>();
        Collection<T> collection1 = new ArrayList<>();
        Collection<T> collection2 = new ArrayList<>();
        Collection<T> collection3 = new ArrayList<>();
        int counter = 0;
        for (T t : splitMe) {
            if (counter < a) {
                collection0.add(t);
                counter++;
                continue;
            }
            if (counter < 2 * a) {
                collection1.add(t);
                counter++;
                continue;
            }
            if (counter < 3 * a) {
                collection2.add(t);
                counter++;
                continue;
            }
            if (counter < 4 * a) {
                collection3.add(t);
                counter++;
            }

        }
        List<Collection<T>> out = new ArrayList<>();
        out.add(collection0);
        out.add(collection1);
        out.add(collection2);
        out.add(collection3);
        return out;
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
            testForSolution(zero, 11 - chosen.size() - i, one, i, xor, chosen, index);
        }
    }

    private void testForSolution(List<DataSet> zero, int zeros, List<DataSet> one, int ones, DataSet xor, Collection<DataSet> chosen, int index) {

        if (zero.size() >= zeros && one.size() >= ones) {

            List<DataSet[]> subSets;
            // TODO: 22.04.2022 use binomial coefficient
            index = getIndex(xor, index);
            List<List<DataSet[]>> list;
            List<DataSet[]> indexModZero;
            List<DataSet[]> indexModOne;
            if (this.keys + 1 - zeros <= 4) {
                checkForLength(zero, zeros, 0, xor, chosen, index, empty, empty);
                return;
            }
            subSets = calcSubsets(one.toArray(DataSet[]::new), ones);
            list = split(subSets, index);
            indexModZero = list.get(0);
            indexModOne = list.get(1);
            checkForLength(zero, zeros, ones, xor, chosen, index, indexModZero, indexModOne);

        }
    }

    private void checkForLength(List<DataSet> zero, int zeros, int ones, DataSet xor,
                                Collection<DataSet> chosen, int index,
                                List<DataSet[]> indexModZero, List<DataSet[]> indexModOne) {
        List<DataSet> zeroSorted = sort(zero, index);
        List<DataSet> zeroZero = getZeros(zeroSorted, index);
        List<DataSet> zeroOne = new ArrayList<>(zeroSorted);
        zeroOne.removeAll(zeroZero);
        for (int i = 0; i < zeros; i++) {
            if (i % 2 == 0) {
                checkForResult(zeroZero, zeros, i, zeroOne, ones, xor, chosen, indexModZero);
            } else {
                checkForResult(zeroZero, zeros, i, zeroOne, ones, xor, chosen, indexModOne);
            }
        }
    }

    private void checkForResult(List<DataSet> zeroZero, int zeros, int x,
                                List<DataSet> zeroOne, int ones, DataSet xor,
                                Collection<DataSet> chosen, List<DataSet[]> indexMod) {
        List<DataSet[]> zeroSubSets;
        List<DataSet[]> oneSubSets;
        zeroSubSets = calcSubsets(zeroZero.toArray(new DataSet[0]), zeros - x);
        oneSubSets = calcSubsets(zeroOne.toArray(DataSet[]::new), x);
        // System.out.println(zeros + " " + x + " " + zeroSubSets.size() + " " + oneSubSets.size());
        checkSolution(zeros, ones, xor, chosen, indexMod, zeroSubSets, oneSubSets);
    }


    private List<List<DataSet[]>> split(List<DataSet[]> dataSets, int index) {
        List<List<DataSet[]>> out = new ArrayList<>();
        List<DataSet[]> zero = new ArrayList<>();
        List<DataSet[]> one = new ArrayList<>();
        for (DataSet[] dataSet : dataSets) {
            boolean b = false;
            for (DataSet set : dataSet) {
                b = b ^ set.getContent()[index];
            }
            if (b) {
                one.add(dataSet);
                continue;
            }
            zero.add(dataSet);
        }
        out.add(zero);
        out.add(one);
        return out;
    }

    private static long binomial(int n, int k) {
        if (k > n - k)
            k = n - k;
        long b = 1;
        for (int i = 1, m = n; i <= k; i++, m--)
            b = b * m / i;
        return b;
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

    private void checkSolution(int zeros, int ones, DataSet xor, Collection<DataSet> chosen,
                               List<DataSet[]> zeroSubSets, List<DataSet[]> zeroZeroSubSets, List<DataSet[]> oneZeroSubSets) {
        if (zeroSubSets.size() == 0) {
            checkSolution(zeros, ones, xor, chosen, zeroZeroSubSets, oneZeroSubSets);
            return;
        }
        if (zeroZeroSubSets.size() == 0) {
            checkSolution(zeros, ones, xor, chosen, zeroSubSets, oneZeroSubSets);
            return;
        }
        if (oneZeroSubSets.size() == 0) {
            checkSolution(zeros, ones, xor, chosen, zeroSubSets, zeroZeroSubSets);
            return;
        }
        for (DataSet[] zeroSubSet : zeroSubSets) {
            for (DataSet[] zeroZeroSubSet : zeroZeroSubSets) {
                for (DataSet[] oneZeroSubSet : oneZeroSubSets) {
                    if (isResult(xor, zeroSubSet, zeroZeroSubSet, oneZeroSubSet)) {
                        printSolution(chosen, zeroSubSet, zeroZeroSubSet, oneZeroSubSet);
                        System.out.println(zeros + " " + ones);
                        System.exit(0);
                    }
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

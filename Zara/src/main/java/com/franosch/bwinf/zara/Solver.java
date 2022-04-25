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

    public Set<DataSet> solve(List<DataSet> dataSets) {
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
        solve(dataSets, true);
        System.out.println(result);
        return result;
    }

    public void solve(List<DataSet> all, boolean dummy) {

        List<DataSet> sorted = sort(all, 0);
        List<DataSet> zero = getZeros(sorted, 0);
        List<DataSet> one = new ArrayList<>(sorted);
        one.removeAll(zero);

        final List<DataSet> sortedZeros = sort(zero, 1);
        final List<DataSet> zeroZeros = getZeros(sortedZeros, 1);
        final List<DataSet> zeroOnes = new ArrayList<>(sortedZeros);
        zeroOnes.removeAll(zeroZeros);
        int numThreads = 12;
        int sizeZero = zero.size()/numThreads;
        for(int i=numThreads; i>=1; i--) {
            List<DataSet> part;
            if(i== numThreads) {
                part = new ArrayList<>(zero);
            } else {
                part = new ArrayList<>(zero.subList(sizeZero * i, zero.size()));
            }
            final int j = i;
            new Thread(() -> {
                List<DataSet> zeroZerosReduced = new ArrayList<>(zeroZeros);
                List<DataSet>  unused = new ArrayList<>(zero);
                unused.removeAll(part);
                zeroZerosReduced.removeAll(unused);
                List<DataSet> zeroOnesReduced = new ArrayList<>(zeroOnes);
                zeroOnesReduced.removeAll(unused);

                testZeros(all, part, zeroZerosReduced, zeroOnesReduced);
            }).start();
        }

        final List<DataSet> sortedOnes = sort(one, 1);
        final List<DataSet> oneZeros = getZeros(sortedOnes, 1);
        final List<DataSet> oneOnes = new ArrayList<>(sortedOnes);
        oneOnes.removeAll(oneZeros);
        int sizeOne = one.size()/numThreads;
        for(int i=numThreads; i>=1; i--) {
            List<DataSet> part;
            if(i== numThreads) {
                part = new ArrayList<>(one);
            } else {
                part = new ArrayList<>(one.subList(sizeOne * i, one.size()));
            }
            final int j = i;
            new Thread(() -> {
                List<DataSet> oneZerosReduced = new ArrayList<>(oneZeros);
                List<DataSet>  unused = new ArrayList<>(zero);
                unused.removeAll(part);
                oneZerosReduced.removeAll(unused);
                List<DataSet> oneOnesReduced = new ArrayList<>(oneOnes);
                oneOnesReduced.removeAll(unused);

                testOnes(all, part, oneZerosReduced, oneOnesReduced);
            }).start();
        }

    }

    private void testOnes(List<DataSet> all, List<DataSet> one, List<DataSet> oneZeros,
                          List<DataSet> oneOnes) {
        List<DataSet> alreadyDone;
        List<DataSet> allCopy;
        List<DataSet> oneCopy;
        List<DataSet> third;
        alreadyDone = new ArrayList<>();
        List<DataSet> oneZeroCopy;
        List<DataSet> oneOneCopy;

        for (DataSet firstChosen : one) {
            alreadyDone.add(firstChosen);
            oneCopy = new ArrayList<>(one);
            oneCopy.removeAll(alreadyDone);

            oneZeroCopy = new ArrayList<>(oneZeros);
            oneZeroCopy.removeAll(alreadyDone);
            oneOneCopy = new ArrayList<>(oneOnes);
            oneOneCopy.removeAll(alreadyDone);

            for (DataSet secondChosen : oneCopy) {
                oneZeroCopy.remove(secondChosen);
                for (DataSet thirdChosen : oneZeroCopy) {
                    third = new ArrayList<>(oneZeroCopy);
                    third.remove(thirdChosen);

                    for (DataSet fourthChosen : third) {
                        allCopy = new ArrayList<>(all);
                        allCopy.removeAll(alreadyDone);
                        allCopy.remove(secondChosen);
                        allCopy.remove(thirdChosen);
                        allCopy.remove(fourthChosen);
                        solve3(allCopy, List.of(firstChosen, secondChosen, thirdChosen, fourthChosen));
                    }
                }
                oneOneCopy.remove(secondChosen);
                for (DataSet thirdChosen : oneOneCopy) {
                    third = new ArrayList<>(oneOneCopy);
                    third.remove(thirdChosen);
                    for (DataSet fourthChosen : third) {
                        allCopy = new ArrayList<>(all);
                        allCopy.removeAll(alreadyDone);
                        allCopy.remove(secondChosen);
                        allCopy.remove(thirdChosen);
                        allCopy.remove(fourthChosen);
                        solve3(allCopy, List.of(firstChosen, secondChosen, thirdChosen, fourthChosen));
                    }
                }

            }
        }
    }

    private void testZeros(List<DataSet> all, List<DataSet> zero, List<DataSet> zeroZeros, List<DataSet> zeroOnes) {
        List<DataSet> allCopy;
        List<DataSet> zeroCopy;


        List<DataSet> alreadyDone = new ArrayList<>();
        List<DataSet> zeroZeroCopy;
        List<DataSet> zeroOneCopy;

        List<DataSet> third;
        for (DataSet firstChosen : zero) {
            alreadyDone.add(firstChosen);
            zeroCopy = new ArrayList<>(zero);
            zeroCopy.removeAll(alreadyDone);
            zeroZeroCopy = new ArrayList<>(zeroZeros);
            zeroZeroCopy.removeAll(alreadyDone);
            zeroOneCopy = new ArrayList<>(zeroOnes);
            zeroOneCopy.removeAll(alreadyDone);

            for (DataSet secondChosen : zeroCopy) {
                zeroZeroCopy.remove(secondChosen);
                for (DataSet thirdChosen : zeroZeroCopy) {
                    third = new ArrayList<>(zeroZeroCopy);
                    third.remove(thirdChosen);
                    for (DataSet fourthChosen : third) {
                        allCopy = new ArrayList<>(all);
                        allCopy.removeAll(alreadyDone);
                        allCopy.remove(secondChosen);
                        allCopy.remove(thirdChosen);
                        allCopy.remove(fourthChosen);
                        solve3(allCopy, List.of(firstChosen, secondChosen, thirdChosen, fourthChosen));
                    }
                }
                zeroOneCopy.remove(secondChosen);
                for (DataSet thirdChosen : zeroOneCopy) {
                    third = new ArrayList<>(zeroOneCopy);
                    third.remove(thirdChosen);
                    for (DataSet fourthChosen : third) {
                        allCopy = new ArrayList<>(all);
                        allCopy.removeAll(alreadyDone);
                        allCopy.remove(secondChosen);
                        allCopy.remove(thirdChosen);
                        allCopy.remove(fourthChosen);
                        solve3(allCopy, List.of(firstChosen, secondChosen, thirdChosen, fourthChosen));
                    }
                }

            }


        }
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


        for (int i = 0; i < keys + 1 - chosen.size() && keys + 1 - chosen.size() - i >= 0; i += 2) {
            testForSolution(zero, keys + 1 - chosen.size() - i, one, i, xor, chosen, index);
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


    private List<DataSet> getZeros(List<DataSet> dataSets, int i) {
        List<DataSet> zero = new ArrayList<>();
        for (DataSet dataSet : dataSets) {
            if (!dataSet.getContent()[i]) {
                zero.add(dataSet);
            }
        }
        return zero;
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

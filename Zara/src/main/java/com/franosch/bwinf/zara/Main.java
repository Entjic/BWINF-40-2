package com.franosch.bwinf.zara;

import com.franosch.bwinf.zara.io.FileReader;
import com.franosch.bwinf.zara.model.DataSet;
import com.franosch.bwinf.zara.model.Mastercard;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {
    public final static String TEST_RESOURCES = "zara/src/test/resources/";


    public static void main(String[] args) {
        random();
    }

    private static void run() {
        Solver solver = new Solver();

        // 11 -> 1, 4, 7, 10, 11, 13, 16, 21, 24, 25, 41
        FileReader fileReader = new FileReader("stapel14", TEST_RESOURCES);
        List<DataSet> dataSets = new ArrayList<>();

        for (String s : fileReader.getContent().subList(1, fileReader.getContent().size())) {
            if (s.startsWith("#")) {
                continue;
            }
            DataSet dataSet = new DataSet(s);
            dataSets.add(dataSet);
        }
        int length = Integer.parseInt(fileReader.getContent().get(0).split(" ")[1]);
        solver.solve(dataSets, length);
    }

    private static void random() {
        int cards = 50;
        int keys = 10;
        DataSet[] random = new DataSet[cards];
        for (int i = 0; i < random.length; i++) {
            random[i] = new DataSet(128);
        }
        DataSet[] key = new DataSet[keys];
        List<DataSet> copy = new CopyOnWriteArrayList<>(Arrays.asList(random));
        for (int i = 0; i < key.length; i++) {
            DataSet dataSet = copy.get(new Random().nextInt(copy.size()));
            copy.remove(dataSet);
            key[i] = dataSet;
        }
        Solver solver = new Solver();
        Mastercard mastercard = new Mastercard(key);
        System.out.println(mastercard);
        DataSet master = mastercard.getContent();
        List<DataSet> combined = new ArrayList<>(Arrays.asList(random));
        combined.add(master);
        Set<DataSet> result = new HashSet<>();


        solver.solve(combined, keys);
        System.out.println(result);
    }

}

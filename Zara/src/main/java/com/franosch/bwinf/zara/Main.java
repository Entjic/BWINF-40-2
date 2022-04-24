package com.franosch.bwinf.zara;

import com.franosch.bwinf.zara.io.FileReader;
import com.franosch.bwinf.zara.model.DataSet;
import com.franosch.bwinf.zara.model.Mastercard;
import lombok.SneakyThrows;

import java.io.File;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {
    public final static String TEST_RESOURCES = "zara/src/test/resources/";


    public static void main(String[] args) {
        String path;
        String name;
        int part;
        boolean test = true;
        if (!test) {
            for (String arg : args) {
                System.out.println(arg);
            }
            name = args[0];
            part = Integer.parseInt(args[1]);
            String pre = getCurrentPath();
            System.out.println(pre);
            path = pre + "rsc/";
        } else {
            name = "stapel5";
            path = TEST_RESOURCES;
            part = 0;
        }
        run(name, path, part);
    }

    private static void run(String name, String path, int part) {

        // 11 -> 1, 4, 7, 10, 11, 13, 16, 21, 24, 25, 41
        FileReader fileReader = new FileReader(name, path);
        List<DataSet> dataSets = new ArrayList<>();

        for (String s : fileReader.getContent().subList(1, fileReader.getContent().size())) {
            if (s.startsWith("#")) {
                continue;
            }
            DataSet dataSet = new DataSet(s);
            dataSets.add(dataSet);
        }
        int length = Integer.parseInt(fileReader.getContent().get(0).split(" ")[1]);
        Solver solver = new Solver(length, part);
        solver.solve(dataSets);
    }

    private static void random() {
        int cards = 40;
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
        Solver solver = new Solver(keys, 0);
        Mastercard mastercard = new Mastercard(key);
        System.out.println(mastercard);
        DataSet master = mastercard.getContent();
        List<DataSet> combined = new ArrayList<>(Arrays.asList(random));
        combined.add(master);
        Set<DataSet> result = new HashSet<>();

        solver.solve(combined);
        System.out.println(result);
    }

    @SneakyThrows
    private static String getCurrentPath() {
        String path = new File(Main.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getPath();
        String[] splits = path.split("[/\\\\]");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < splits.length - 1; i++) {
            stringBuilder.append(splits[i]).append("/");
        }
        return stringBuilder.toString();
    }

}

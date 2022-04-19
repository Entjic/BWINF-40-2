package com.franosch.bwinf.zara;

import com.franosch.bwinf.zara.model.DataSet;
import com.franosch.bwinf.zara.model.Mastercard;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {

    public static void main(String[] args) {
        DataSet[] random = new DataSet[100];
        for (int i = 0; i < random.length; i++) {
            random[i] = new DataSet();
        }
        DataSet[] key = new DataSet[2];
        List<DataSet> copy = new CopyOnWriteArrayList<>(Arrays.asList(random));
        for (int i = 0; i < key.length; i++) {
            DataSet dataSet = copy.get(new Random().nextInt(copy.size()));
            copy.remove(dataSet);
            key[i] = dataSet;
        }
        Mastercard mastercard = new Mastercard(key);
        System.out.println(mastercard);
        DataSet master = mastercard.getContent();
        List<DataSet> combined = new ArrayList<>(Arrays.asList(random));
        combined.add(master);
        Solver solver = new Solver();
        Set<DataSet> result = new HashSet<>();
        solver.solve(combined, 0, 1, 3, result, new ArrayList<>());
        System.out.println(result);
    }

}

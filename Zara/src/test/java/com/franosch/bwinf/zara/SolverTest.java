package com.franosch.bwinf.zara;

import com.franosch.bwinf.zara.io.FileReader;
import com.franosch.bwinf.zara.model.DataSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SolverTest {
    public final static String TEST_RESOURCES = "src/test/resources/";
    public static int keyLength = 128;


    @Test
    void playground() {
        // 10 -> 11 25 29 16 19 22 12 21 18 17 30
        FileReader fileReader = new FileReader("stapel10", TEST_RESOURCES);
        List<DataSet> dataSets = new ArrayList<>();

        for (String s : fileReader.getContent().subList(1, fileReader.getContent().size())) {
            DataSet dataSet = new DataSet(s);
            dataSets.add(dataSet);
        }
        int length = Integer.parseInt(fileReader.getContent().get(0).split(" ")[1]);
        Solver solver = new Solver(length);
        Set<DataSet> result = solver.solve(dataSets);
        Assertions.assertEquals(length + 1, result.size());

    }

    @Test
    void playground2() {
        // 11 -> 0, 1, 2, 8, 9, 13, 14, 16, 18, 19, 15
        FileReader fileReader = new FileReader("stapel11", TEST_RESOURCES);
        List<DataSet> dataSets = new ArrayList<>();

        for (String s : fileReader.getContent().subList(1, fileReader.getContent().size())) {
            DataSet dataSet = new DataSet(s);
            dataSets.add(dataSet);
        }
        int length = Integer.parseInt(fileReader.getContent().get(0).split(" ")[1]);
        Solver solver = new Solver(length);
        Set<DataSet> result = solver.solve(dataSets);
        Assertions.assertEquals(length + 1, result.size());

    }

    @Test
    void playground12() {
        // 11 -> 1, 4, 7, 10, 11, 13, 16, 21, 24, 25, 26
        FileReader fileReader = new FileReader("stapel12", TEST_RESOURCES);
        List<DataSet> dataSets = new ArrayList<>();

        for (String s : fileReader.getContent().subList(1, fileReader.getContent().size())) {
            if (s.startsWith("#")) {
                continue;
            }
            DataSet dataSet = new DataSet(s);
            dataSets.add(dataSet);
        }
        int length = Integer.parseInt(fileReader.getContent().get(0).split(" ")[1]);
        Solver solver = new Solver(length);
        Set<DataSet> result = solver.solve(dataSets);
        Assertions.assertEquals(length + 1, result.size());

    }

    @Test
    void playground13() {
        // 11 -> 11, 2, 16, 23, 7, 8, 19, 35, 37, 32, 40
        FileReader fileReader = new FileReader("stapel13", TEST_RESOURCES);
        List<DataSet> dataSets = new ArrayList<>();

        for (String s : fileReader.getContent().subList(1, fileReader.getContent().size())) {
            if (s.startsWith("#")) {
                continue;
            }
            DataSet dataSet = new DataSet(s);
            dataSets.add(dataSet);
        }
        int length = Integer.parseInt(fileReader.getContent().get(0).split(" ")[1]);
        Solver solver = new Solver(length);
        Set<DataSet> result = solver.solve(dataSets);
        Assertions.assertEquals(length + 1, result.size());
    }

    @Test
    void test() {
        // sol 16 7 5 13 11 18 20 f h l n q s u
        // 32 length dataset
        boolean[] a = new boolean[]{false, true, true, true, true, false, false, true, false, false, true, false, true, false, false, true, false, false, false, false, true, true, true, true, false, false, true, true, false, true, true, true};
        boolean[] b = new boolean[]{false, true, true, true, false, true, true, true, true, true, false, true, false, true, true, true, false, false, false, true, true, false, true, false, true, false, false, false, true, false, false, true};
        boolean[] c = new boolean[]{false, false, false, true, false, true, false, true, false, false, false, false, true, true, true, true, true, false, false, true, false, true, false, true, false, false, false, false, true, true, false, false};
        boolean[] d = new boolean[]{true, false, false, false, false, true, true, false, false, false, true, true, true, false, true, true, true, false, false, false, false, false, false, false, true, false, false, true, false, true, false, true};
        boolean[] e = new boolean[]{false, false, false, true, false, false, true, false, false, false, false, false, true, false, false, false, false, true, true, true, false, true, false, false, true, true, false, false, false, false, false, true};
        boolean[] f = new boolean[]{true, false, true, false, true, false, false, false, false, false, true, true, false, false, true, false, false, true, true, true, false, true, true, true, true, true, true, true, false, false, true, false};
        boolean[] g = new boolean[]{true, false, true, true, false, false, true, true, false, false, true, true, false, false, true, false, false, false, true, true, true, false, true, false, true, true, false, false, true, false, false, false};
        boolean[] h = new boolean[]{true, false, false, false, true, true, false, true, false, true, true, false, false, false, true, false, false, true, false, false, true, false, false, true, true, true, false, true, false, true, true, true};
        boolean[] i = new boolean[]{false, false, false, false, true, true, false, false, true, true, false, true, true, true, false, false, false, false, true, false, false, false, false, true, true, false, true, true, false, true, false, true};
        boolean[] j = new boolean[]{false, false, true, true, true, true, false, true, false, true, false, true, true, true, false, true, false, true, false, false, true, true, false, true, false, true, false, false, false, false, true, false};
        boolean[] k = new boolean[]{false, false, true, true, false, false, true, true, false, true, false, false, true, false, true, false, false, false, false, true, false, false, false, true, false, false, true, false, false, true, true, true};
        boolean[] l = new boolean[]{true, true, false, true, false, true, true, true, true, false, true, true, true, true, false, false, false, true, false, true, true, false, true, false, true, true, true, true, true, true, false, true};
        boolean[] m = new boolean[]{true, false, false, true, true, true, true, true, false, true, true, false, false, false, false, true, true, false, true, true, false, false, false, false, false, true, true, true, true, true, true, false};
        boolean[] n = new boolean[]{false, true, false, false, true, false, false, false, false, false, false, true, false, true, false, false, false, false, true, true, true, false, false, false, false, false, true, false, true, true, false, false};
        boolean[] o = new boolean[]{false, false, false, true, false, false, false, false, true, false, false, false, true, false, false, true, true, false, true, false, false, false, false, false, true, true, false, true, false, true, false, true};
        boolean[] p = new boolean[]{true, false, true, true, false, false, true, true, true, false, true, true, false, false, false, false, true, false, true, true, true, true, true, true, false, true, true, false, false, true, true, false};
        boolean[] q = new boolean[]{true, false, false, false, false, true, false, false, true, false, false, false, true, false, false, false, true, false, false, true, true, false, true, true, true, true, false, false, false, false, true, false};
        boolean[] r = new boolean[]{false, false, true, true, true, false, true, false, false, false, true, true, false, false, false, false, false, true, false, true, false, false, true, false, true, false, false, false, true, false, false, true};
        boolean[] s = new boolean[]{false, true, true, false, false, false, true, true, true, true, true, false, false, true, true, false, false, true, false, false, true, true, false, true, true, true, false, false, false, true, true, true};
        boolean[] t = new boolean[]{false, false, false, false, false, true, true, false, false, false, true, false, true, false, true, true, false, true, true, true, true, false, true, false, true, true, false, true, true, false, false, true};
        boolean[] u = new boolean[]{false, true, false, true, true, true, false, true, true, false, false, true, false, true, true, false, true, false, false, false, true, false, true, false, true, true, true, true, false, false, false, true};
        List<DataSet> combined = new ArrayList<>();
        List.of(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)
                .forEach(booleans -> combined.add(new DataSet(booleans)));
        Solver solver = new Solver(6);
        Set<DataSet> result = solver.solve(combined);
        Assertions.assertEquals(7, result.size());

    }

    @Test
    void test1() {
        // sol a - j
        // 32 length dataset
        boolean[] master = new boolean[]{false, false, true, false, true, false, false, false, false, false, true, false, false, false, false, true, false, false, false, true, true, true, false, false, true, true, true, true, true, true, false, false};
        boolean[] a = new boolean[]{false, false, true, true, true, false, false, true, false, false, true, false, true, false, false, true, false, false, false, false, true, true, true, true, false, false, true, true, false, true, true, true};
        boolean[] b = new boolean[]{false, false, true, true, false, true, true, true, true, true, false, true, false, true, true, true, false, false, false, true, true, false, true, false, true, false, false, false, true, false, false, true};
        boolean[] c = new boolean[]{false, false, false, true, false, true, false, true, false, false, false, false, true, true, true, true, true, false, false, true, false, true, false, true, false, false, false, false, true, true, false, false};
        boolean[] d = new boolean[]{false, false, false, false, false, true, true, false, false, false, true, true, true, false, true, true, true, false, false, false, false, false, false, false, true, false, false, true, false, true, false, true};
        boolean[] e = new boolean[]{false, false, false, true, false, false, true, false, false, false, false, false, true, false, false, false, false, true, true, true, false, true, false, false, true, true, false, false, false, false, false, true};
        boolean[] f = new boolean[]{false, false, true, false, true, false, false, false, false, false, true, true, false, false, true, false, false, true, true, true, false, true, true, true, true, true, true, true, false, false, true, false};
        boolean[] g = new boolean[]{false, false, true, true, false, false, true, true, false, false, true, true, false, false, true, false, false, false, true, true, true, false, true, false, true, true, false, false, true, false, false, false};
        boolean[] h = new boolean[]{false, false, false, false, true, true, false, true, false, true, true, false, false, false, true, false, false, true, false, false, true, false, false, true, true, true, false, true, false, true, true, true};
        boolean[] i = new boolean[]{false, false, false, false, true, true, false, false, true, true, false, true, true, true, false, false, false, false, true, false, false, false, false, true, true, false, true, true, false, true, false, true};
        boolean[] j = new boolean[]{false, false, true, true, true, true, false, true, false, true, false, true, true, true, false, true, false, true, false, false, true, true, false, true, false, true, false, false, false, false, true, false};
        boolean[] k = new boolean[]{false, false, true, true, false, false, true, true, false, true, false, false, true, false, true, false, false, false, false, true, false, false, false, true, false, false, true, false, false, true, true, true};
        boolean[] l = new boolean[]{true, true, false, true, false, true, true, true, true, false, true, true, true, true, false, false, false, true, false, true, true, false, true, false, true, true, true, true, true, true, false, true};
        boolean[] m = new boolean[]{true, false, false, true, true, true, true, true, false, true, true, false, false, false, false, true, true, false, true, true, false, false, false, false, false, true, true, true, true, true, true, false};
        boolean[] n = new boolean[]{false, true, false, false, true, false, false, false, false, false, false, true, false, true, false, false, false, false, true, true, true, false, false, false, false, false, true, false, true, true, false, false};
        boolean[] o = new boolean[]{false, false, false, true, false, false, false, false, true, false, false, false, true, false, false, true, true, false, true, false, false, false, false, false, true, true, false, true, false, true, false, true};
        boolean[] p = new boolean[]{true, false, true, true, false, false, true, true, true, false, true, true, false, false, false, false, true, false, true, true, true, true, true, true, false, true, true, false, false, true, true, false};
        boolean[] q = new boolean[]{true, false, false, false, false, true, false, false, true, false, false, false, true, false, false, false, true, false, false, true, true, false, true, true, true, true, false, false, false, false, true, false};
        boolean[] r = new boolean[]{false, false, true, true, true, false, true, false, false, false, true, true, false, false, false, false, false, true, false, true, false, false, true, false, true, false, false, false, true, false, false, true};
        boolean[] s = new boolean[]{false, true, true, false, false, false, true, true, true, true, true, false, false, true, true, false, false, true, false, false, true, true, false, true, true, true, false, false, false, true, true, true};
        boolean[] t = new boolean[]{false, false, false, false, false, true, true, false, false, false, true, false, true, false, true, true, false, true, true, true, true, false, true, false, true, true, false, true, true, false, false, true};
        boolean[] u = new boolean[]{false, true, false, true, true, true, false, true, true, false, false, true, false, true, true, false, true, false, false, false, true, false, true, false, true, true, true, true, false, false, false, true};
        List<DataSet> combined = new ArrayList<>();
        List.of(master, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)
                .forEach(booleans -> combined.add(new DataSet(booleans)));
        Solver solver = new Solver(10);
        Set<DataSet> result = solver.solve(combined);
        Assertions.assertEquals(11, result.size());

    }

    @Test
    void test2() {
        // sol a - j
        // 32 length dataset
        boolean[] master = new boolean[]{false, false, true, false, true, false, false, false, false, false, true, false, false, false, false, true, false, false, false, true, true, true, false, false, true, true, true, true, true, true, false, false};
        boolean[] a = new boolean[]{false, true, true, true, true, false, false, true, false, false, true, false, true, false, false, true, false, false, false, false, true, true, true, true, false, false, true, true, false, true, true, true};
        boolean[] b = new boolean[]{false, true, true, true, false, true, true, true, true, true, false, true, false, true, true, true, false, false, false, true, true, false, true, false, true, false, false, false, true, false, false, true};
        boolean[] c = new boolean[]{false, false, false, true, false, true, false, true, false, false, false, false, true, true, true, true, true, false, false, true, false, true, false, true, false, false, false, false, true, true, false, false};
        boolean[] d = new boolean[]{false, false, false, false, false, true, true, false, false, false, true, true, true, false, true, true, true, false, false, false, false, false, false, false, true, false, false, true, false, true, false, true};
        boolean[] e = new boolean[]{false, false, false, true, false, false, true, false, false, false, false, false, true, false, false, false, false, true, true, true, false, true, false, false, true, true, false, false, false, false, false, true};
        boolean[] f = new boolean[]{false, false, true, false, true, false, false, false, false, false, true, true, false, false, true, false, false, true, true, true, false, true, true, true, true, true, true, true, false, false, true, false};
        boolean[] g = new boolean[]{false, false, true, true, false, false, true, true, false, false, true, true, false, false, true, false, false, false, true, true, true, false, true, false, true, true, false, false, true, false, false, false};
        boolean[] h = new boolean[]{false, false, false, false, true, true, false, true, false, true, true, false, false, false, true, false, false, true, false, false, true, false, false, true, true, true, false, true, false, true, true, true};
        boolean[] i = new boolean[]{false, false, false, false, true, true, false, false, true, true, false, true, true, true, false, false, false, false, true, false, false, false, false, true, true, false, true, true, false, true, false, true};
        boolean[] j = new boolean[]{false, false, true, true, true, true, false, true, false, true, false, true, true, true, false, true, false, true, false, false, true, true, false, true, false, true, false, false, false, false, true, false};
        boolean[] k = new boolean[]{false, false, true, true, false, false, true, true, false, true, false, false, true, false, true, false, false, false, false, true, false, false, false, true, false, false, true, false, false, true, true, true};
        boolean[] l = new boolean[]{true, true, false, true, false, true, true, true, true, false, true, true, true, true, false, false, false, true, false, true, true, false, true, false, true, true, true, true, true, true, false, true};
        boolean[] m = new boolean[]{true, false, false, true, true, true, true, true, false, true, true, false, false, false, false, true, true, false, true, true, false, false, false, false, false, true, true, true, true, true, true, false};
        boolean[] n = new boolean[]{false, true, false, false, true, false, false, false, false, false, false, true, false, true, false, false, false, false, true, true, true, false, false, false, false, false, true, false, true, true, false, false};
        boolean[] o = new boolean[]{false, false, false, true, false, false, false, false, true, false, false, false, true, false, false, true, true, false, true, false, false, false, false, false, true, true, false, true, false, true, false, true};
        boolean[] p = new boolean[]{true, false, true, true, false, false, true, true, true, false, true, true, false, false, false, false, true, false, true, true, true, true, true, true, false, true, true, false, false, true, true, false};
        boolean[] q = new boolean[]{true, false, false, false, false, true, false, false, true, false, false, false, true, false, false, false, true, false, false, true, true, false, true, true, true, true, false, false, false, false, true, false};
        boolean[] r = new boolean[]{false, false, true, true, true, false, true, false, false, false, true, true, false, false, false, false, false, true, false, true, false, false, true, false, true, false, false, false, true, false, false, true};
        boolean[] s = new boolean[]{false, true, true, false, false, false, true, true, true, true, true, false, false, true, true, false, false, true, false, false, true, true, false, true, true, true, false, false, false, true, true, true};
        boolean[] t = new boolean[]{false, false, false, false, false, true, true, false, false, false, true, false, true, false, true, true, false, true, true, true, true, false, true, false, true, true, false, true, true, false, false, true};
        boolean[] u = new boolean[]{false, true, false, true, true, true, false, true, true, false, false, true, false, true, true, false, true, false, false, false, true, false, true, false, true, true, true, true, false, false, false, true};
        List<DataSet> combined = new ArrayList<>();

        // Mastercard mastercard = new Mastercard(Stream.of(a, b, c, d, e, f, g, h, i, j).map(DataSet::new).toArray(DataSet[]::new));
        // System.out.println(mastercard.getContent());

        List.of(master, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)
                .forEach(booleans -> combined.add(new DataSet(booleans)));
        Solver solver = new Solver(10);
        Set<DataSet> result = solver.solve(combined);
        Assertions.assertEquals(11, result.size());

    }

}

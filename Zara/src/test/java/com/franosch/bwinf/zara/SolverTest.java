package com.franosch.bwinf.zara;

import com.franosch.bwinf.zara.model.DataSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SolverTest {
    private static Solver solver;

    @BeforeAll
    static void setUp() {
        solver = new Solver();
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
        Set<DataSet> result = solver.solve(combined, 6);
        Assertions.assertEquals(7, result.size());

    }

}

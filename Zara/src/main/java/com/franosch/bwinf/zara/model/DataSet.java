package com.franosch.bwinf.zara.model;


import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;


@Getter
public class DataSet implements Comparable {


    private static final AtomicInteger atomicInteger = new AtomicInteger();
    private final int id = atomicInteger.getAndIncrement();
    private final boolean[] content;
    private final int keyLength;

    public DataSet(boolean[] content, int keyLength) {
        this.keyLength = keyLength;
        this.content = content;
    }

    public DataSet(boolean[] content) {
        this.keyLength = content.length;
        this.content = content;
    }

    public DataSet(int keyLength) {
        this.keyLength = keyLength;
        this.content = new boolean[keyLength];
        for (int i = 0; i < content.length; i++) {
            content[i] = new Random().nextBoolean();
        }
    }

    public DataSet(String input) {
        this.keyLength = input.length();
        char[] charArray = input.toCharArray();
        boolean[] content = new boolean[keyLength];
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            boolean b = false;
            if (c == '1') b = true;
            content[i] = b;
        }
        this.content = content;
    }

    public DataSet(boolean allNull, int keyLength) {
        this.keyLength = keyLength;
        this.content = new boolean[keyLength];
        Set<DataSet[]> set = new HashSet<>();
    }

    @Override
    public int compareTo(Object o) {
        int other = ((DataSet) o).id;
        if (id > other) {
            return 1;
        }
        if (id == other) {
            return 0;
        }
        return -1;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (boolean b : content) {
            String s = "0";
            if(b) s = "1";
            stringBuilder.append(s);
        }
        return id + " " +
                stringBuilder;
    }
}

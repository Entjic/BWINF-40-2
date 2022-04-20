package com.franosch.bwinf.zara.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


@Getter
@RequiredArgsConstructor
public class DataSet {

    public static int keyLength =128;

    private static final AtomicInteger atomicInteger = new AtomicInteger();
    private final boolean[] content;
    private final int id = atomicInteger.getAndIncrement();

    public DataSet() {
        content = new boolean[keyLength];
        for (int i = 0; i < content.length; i++) {
            content[i] = new Random().nextBoolean();
        }
    }

    public DataSet(boolean allNull) {
        content = new boolean[keyLength];
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (boolean b : content) {
            stringBuilder.append(b).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return "DataSet{" +
                id + " " +
                stringBuilder +
                '}';
    }
}

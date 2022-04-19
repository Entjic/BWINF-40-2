package com.franosch.bwinf.zara.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


@Getter
@RequiredArgsConstructor
public class DataSet {

    public static int keyLength = 128;

    private static final AtomicInteger atomicInteger = new AtomicInteger();
    private final boolean[] content;
    private final int id = atomicInteger.getAndIncrement();

    public DataSet() {
        content = new boolean[keyLength];
        for (int i = 0; i < content.length; i++) {
            content[i] = new Random().nextBoolean();
        }
    }

    @Override
    public String toString() {
        return "DataSet{" +
                "content=" + Arrays.toString(content) +
                ", id=" + id +
                '}';
    }
}

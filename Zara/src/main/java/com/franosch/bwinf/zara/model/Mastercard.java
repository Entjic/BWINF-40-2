package com.franosch.bwinf.zara.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public class Mastercard {
    private final DataSet[] dataSets;
    private final DataSet content;

    public Mastercard(DataSet[] dataSets) {
        this.dataSets = dataSets;
        content = generate(dataSets);
    }

    private DataSet generate(DataSet[] dataSets) {
        int length = dataSets[0].getKeyLength();
        boolean[] content = new boolean[length];
        for (int i = 0; i < length; i++) {
            boolean c = false;
            for (DataSet dataSet : dataSets) {
                c = dataSet.getContent()[i] ^ c;
            }
            content[i] = c;
        }
        return new DataSet(content, length);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (DataSet dataSet : dataSets) {
            stringBuilder.append("\n").append(dataSet.getId()).append(" content").append(Arrays.toString(dataSet.getContent()));
        }
        stringBuilder.append("\n").append("xor ").append(content);
        return "Mastercard{" +
                stringBuilder +
                '}';
    }
}

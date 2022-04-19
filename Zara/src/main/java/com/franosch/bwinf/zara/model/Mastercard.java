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
        boolean[] content = new boolean[DataSet.keyLength];
        for (int i = 0; i < DataSet.keyLength; i++) {
            boolean c = false;
            for (DataSet dataSet : dataSets) {
                c = dataSet.getContent()[i] ^ c;
            }
            content[i] = c;
        }
        return new DataSet(content);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (DataSet dataSet : dataSets) {
            stringBuilder.append("\n").append(dataSet.getId()).append(" content").append(Arrays.toString(dataSet.getContent()));
        }
        stringBuilder.append("\n").append(content);
        return "Mastercard{" +
                stringBuilder +
                '}';
    }
}

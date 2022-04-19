package com.franosch.bwinf.muellabfuhr.io;

import com.franosch.bwinf.muellabfuhr.model.Result;
import com.franosch.bwinf.muellabfuhr.model.Runner;
import com.franosch.bwinf.muellabfuhr.model.graph.Node;
import com.franosch.bwinf.muellabfuhr.model.graph.Path;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class ResultFileWriter {
    private final Result result;
    private final int testCase;
    private final String path;

    private double worstWeight = 0;

    @SneakyThrows
    public void writeFile() {
        File file = new File(path + "out/result" + testCase + ".txt");
        PrintWriter fileWriter = new PrintWriter(file);
        int i = 1;
        for (Runner runner : result.getRunners()) {
            fileWriter.println("Tag " + i + ": " + generateRunnerOut(runner));
            i++;
        }
        fileWriter.println("Maximale Laenge einer Tagestour: " + (int) worstWeight);
        fileWriter.close();
    }

    private String generateRunnerOut(Runner runner) {
        StringBuilder stringBuilder = new StringBuilder();
        Path path = runner.getFinalPath();
        System.out.println(runner.calcWeight());
        if (path.getWeight() > worstWeight) {
            worstWeight = path.getWeight();
        }
        for (Node node : path.getPath()) {
            stringBuilder.append(node.getId()).append(" -> ");
        }
        String string = stringBuilder.substring(0, stringBuilder.length() - 3);
        stringBuilder = new StringBuilder();
        stringBuilder.append("Gesamtlange: ").append((int) path.getWeight()).append(" Pfad: ").append(string);
        return stringBuilder.toString();
    }


}

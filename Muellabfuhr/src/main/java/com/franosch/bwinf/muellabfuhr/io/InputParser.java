package com.franosch.bwinf.muellabfuhr.io;

import lombok.Getter;

import java.util.*;

public class InputParser {
    private final FileReader fileReader;
    @Getter
    private int nodes, edges;

    public InputParser(FileReader fileReader){
        this.fileReader = fileReader;
    }

    public Map<String, Set<InputString>> parse(){
        Map<String, Set<InputString>> map = new HashMap<>();
        List<String> content = fileReader.getContent();
        edges = Integer.parseInt(content.get(0).split(" ")[0]);
        edges = Integer.parseInt(content.get(0).split(" ")[1]);
        content.remove(0);
        for (String input : content) {
            String[] split = input.split(" ");

            InputString inputStringDir = new InputString(split[0], split[1], split[2]);
            Set<InputString> setDir = map.computeIfAbsent(split[0], k -> new HashSet<>());
            setDir.add(inputStringDir);

            InputString inputStringInDir = new InputString(split[1], split[0], split[2]);
            Set<InputString> setInDir = map.computeIfAbsent(split[1], k -> new HashSet<>());
            setInDir.add(inputStringInDir);
        }
        return map;
    }

}

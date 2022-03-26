package com.franosch.bwinf.muellabfuhr.model;


import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;


@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public class Edge{
    private final Path path;
    private final double weight;

    public static Neighbor create(Node from, Node to, double weight){
        return new Neighbor(from, to, weight);
    }

    public static Edge create(Node[] via, double weight){
        final Path path = new Path(weight, via);
        return new Edge(path, weight);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "path=" + path +
                ", weight=" + weight +
                '}';
    }
}

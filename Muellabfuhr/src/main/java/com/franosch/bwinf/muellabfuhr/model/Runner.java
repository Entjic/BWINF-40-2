package com.franosch.bwinf.muellabfuhr.model;

import com.franosch.bwinf.muellabfuhr.model.graph.Circle;
import com.franosch.bwinf.muellabfuhr.model.graph.Graph;
import com.franosch.bwinf.muellabfuhr.model.graph.Node;
import com.franosch.bwinf.muellabfuhr.model.graph.Path;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Runner {
    @Getter
    private final List<Circle> circles;
    private final Graph graph;

    @Getter
    private Path bridge;

    public Runner(Graph graph) {
        this.circles = new ArrayList<>();
        this.graph = graph;
    }

    public double calcWeight() {
        double weight = sumCircles();
        if (bridge == null) {
            bridge = calcBridge();
            if (bridge == null) return weight;
        }
        weight += bridge.getWeight() * 2;
        return weight;
    }

    public Path calcFinalPath() {
        if (bridge == null) {
            bridge = calcBridge();
        }
        // TODO: 14.04.2022 implement me
        return new Path(calcWeight());
    }

    private Path calcBridge() {
        Set<Node> set = new HashSet<>();
        for (Circle circle : circles) {
            set.addAll(circle.getNodes());
        }
        Node min = null;
        double local_weight = Double.MAX_VALUE;
        for (Node node : set) {
            double currentWeight = graph.getWeight(0, node.getId());
            if (currentWeight < local_weight) {
                local_weight = currentWeight;
                min = node;
            }
        }
        if (min == null) return null;
        return graph.getShortestPath(0, min.getId());
    }

    private double sumCircles() {
        double weight = 0;
        for (Circle circle : circles) {
            weight += circle.weight();
        }
        return weight;
    }

    @Override
    public String toString() {
        return "Runner{" +
                "circles=" + circles +
                ", bridge=" + bridge +
                '}';
    }
}

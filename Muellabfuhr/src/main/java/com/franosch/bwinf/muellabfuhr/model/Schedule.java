package com.franosch.bwinf.muellabfuhr.model;

import com.franosch.bwinf.muellabfuhr.model.graph.Circle;
import com.franosch.bwinf.muellabfuhr.model.graph.Path;

import java.util.List;

public record Schedule(List<Path> bridges, List<Circle> circles) {

    public Path getCombinedPath() {
        // TODO: 13.04.2022 implement logic
        return new Path(0);
    }

    public double getCombinedWeight() {
        return 2 * sumBridges() + sumCircles();
    }

    private double sumCircles() {
        double weight = 0;
        for (Circle circle : circles) {
            weight += circle.weight();
        }
        return weight;
    }

    private double sumBridges() {
        double weight = 0;
        for (Path path : bridges) {
            weight += path.getWeight();
        }
        return weight;
    }

}

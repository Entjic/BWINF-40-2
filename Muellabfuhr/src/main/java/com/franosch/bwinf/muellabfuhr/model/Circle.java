package com.franosch.bwinf.muellabfuhr.model;

import java.util.List;

public record Circle(List<Edge> edges, double weight) {
    @Override
    public String toString() {
        return "Circle{" +
                "weight=" + weight +
                ", edges=" + edges +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Circle circle = (Circle) o;

        if (Double.compare(circle.weight, weight) != 0) return false;
        return edges.equals(circle.edges);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = edges.hashCode();
        temp = Double.doubleToLongBits(weight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

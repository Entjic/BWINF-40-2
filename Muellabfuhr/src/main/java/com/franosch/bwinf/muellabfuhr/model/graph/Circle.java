package com.franosch.bwinf.muellabfuhr.model.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Circle {
    private final List<Edge> edges;
    private final double weight;

    public Circle(List<Edge> edges, double weight) {
        this.edges = edges;
        this.weight = weight;
    }

    public Set<Node> getNodes() {
        Set<Node> out = new HashSet<>();
        Node start = findStart();
        Node current = start;
        for (Edge edge : edges) {
            out.add(current);
            current = edge.getEnd(current);
        }
        return out;
    }

    private Node findStart() {
        Edge zero = edges.get(0);
        Edge one = edges.get(1);
        if (zero.getPath().getTo().equals(one.getPath().getFrom())) {
            return zero.getPath().getFrom();
        }
        if (zero.getPath().getTo().equals(one.getPath().getTo())) {
            return zero.getPath().getFrom();
        }
        return zero.getPath().getTo();
    }

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

    public List<Edge> edges() {
        return edges;
    }

    public double weight() {
        return weight;
    }

}

package com.franosch.bwinf.muellabfuhr.model.graph;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Cycle {
    private final double weight;
    private List<Edge> edges;
    @Getter
    private final int id;
    private static AtomicInteger counter = new AtomicInteger();

    public Cycle(List<Edge> edges, double weight) {
        this.edges = edges;
        this.weight = weight;
        this.id = counter.incrementAndGet();
    }

    public Cycle(List<Edge> edges) {
        double w = 0;
        for (Edge edge : edges) {
            w += edge.getPath().getWeight();
        }
        this.weight = w;
        this.edges = edges;
        this.id = counter.incrementAndGet();

    }

    public void sortStartingWith(Node start) {
        List<Edge> edges = new ArrayList<>();
        Node current = start;
        Edge edge = find(current);
        Node next = edge.getEnd(current);
        edges.add(edge);
        List<Edge> list = new ArrayList<>();
        list.add(edge);
        while (list.size() != this.edges.size()) {
            current = next;
            edge = find(list, current);
            next = edge.getEnd(next);
            list.add(edge);
            edges.add(edge);
        }

        this.edges = edges;
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

    public Edge find(Node from) {
        for (Edge edge : edges) {
            if (edge.getPath().getFrom().equals(from) || edge.getPath().getTo().equals(from)) return edge;
        }
        return null;
    }

    public Edge find(Collection<Edge> closed, Node from) {
        List<Edge> edges = new ArrayList<>(edges());
        for (Edge edge : closed) {
            int index = edges.lastIndexOf(edge);
            edges.remove(index);
        }
        for (Edge edge : edges) {
            if (edge.getPath().getFrom().equals(from)) return edge;
            if (edge.getPath().getTo().equals(from)) return edge;
        }
        return null;
    }


    public boolean isCircleNode(Node node) {
        for (Node circleNode : getNodes()) {
            if (circleNode.equals(node)) return true;
        }
        return false;
    }

    public int getLength() {
        return edges.size();
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

        Cycle cycle = (Cycle) o;

        if (Double.compare(cycle.weight, weight) != 0) return false;
        return edges.equals(cycle.edges);
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

package com.franosch.bwinf.muellabfuhr.model.graph;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Edge {
    private final Path path;

    public static Neighbor create(Node from, Node to, double weight) {
        return new Neighbor(from, to, weight);
    }

    public static Edge create(Node[] via, double weight) {
        final Path path = new Path(weight, via);
        return new Edge(path);
    }

    public static Edge create(Path path, double weight) {
        return new Edge(path);
    }

    public Node getEnd(Node start) {
        if (path.getFrom().getId() == start.getId()) {
            return path.getTo();
        }
        if (path.getTo().getId() == start.getId()) {
            return path.getFrom();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        return path.equals(edge.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        return "Edge{" +
                "from " + path.getFrom() + " to " + path.getTo() +
                '}';
    }
}

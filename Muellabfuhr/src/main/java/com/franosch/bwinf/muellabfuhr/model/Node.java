package com.franosch.bwinf.muellabfuhr.model;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
public class Node {

    private final int id;
    private final List<Edge> edges;
    @Setter
    private DijkstraNode dijkstraNode;

    public Node(int id){
        this.id = id;
        this.edges = new ArrayList<>();
        this.dijkstraNode = new DijkstraNode(id);
    }

    public void appendEdge(Edge edge){
        this.edges.add(edge);
    }

    public int getDegree(){
        return edges.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                '}';
    }
}

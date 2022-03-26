package com.franosch.bwinf.muellabfuhr.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
public class Node {

    private final int id;
    private final Set<Neighbor> neighbors;
    @Setter
    private DijkstraNode dijkstraNode;

    public Node(int id){
        this.id = id;
        this.neighbors = new HashSet<>();
        this.dijkstraNode = new DijkstraNode(id);
    }

    public void appendNeighbor(Neighbor neighbor){
        this.neighbors.add(neighbor);
    }

    public int getDegree(){
        return neighbors.size();
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

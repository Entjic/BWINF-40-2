package com.franosch.bwinf.muellabfuhr.model.graph;

public class Neighbor extends Edge{
    protected Neighbor(Node from, Node to, double weight) {
        super(new Path(weight, from, to));
    }

    public Node getNeighbor(Node current){
        return super.getPath().getFrom().equals(current) ? super.getPath().getTo() : super.getPath().getFrom();
    }
}

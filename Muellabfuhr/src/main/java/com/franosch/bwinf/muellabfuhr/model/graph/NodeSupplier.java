package com.franosch.bwinf.muellabfuhr.model.graph;

import java.util.function.Supplier;

public class NodeSupplier implements Supplier<Node> {
    private int id;

    @Override
    public Node get() {
        Node node = new Node(id);
        id++;
        return node;
    }
}

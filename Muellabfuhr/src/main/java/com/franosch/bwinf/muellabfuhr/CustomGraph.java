package com.franosch.bwinf.muellabfuhr;

import org.jgrapht.GraphType;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultGraphType;

import java.util.function.Supplier;

public class CustomGraph<V, E> extends AbstractBaseGraph<V, E> {
    protected CustomGraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier) {
        super(vertexSupplier, edgeSupplier, new DefaultGraphType.Builder().allowCycles(true)
                .allowMultipleEdges(true).allowSelfLoops(true).weighted(true).build());
    }
}

package com.franosch.bwinf.muellabfuhr.model.graph;

import java.util.*;

public class DijkstraGraph {
    private final Graph graph;
    private final Integer source;
    private final Map<Integer, DijkstraNode> nodes;

    public DijkstraGraph(Graph graph, Integer source) {
        this.graph = graph;
        this.source = source;
        this.nodes = initNodes();
    }

    private Map<Integer, DijkstraNode> initNodes() {
        final Map<Integer, DijkstraNode> map = new HashMap<>();
        for (Integer integer : graph.getNodes().keySet()) {
            DijkstraNode node = new DijkstraNode(integer);
            map.put(integer, node);
        }
        return map;
    }

    public void generateShortestPaths() {
        PriorityQueue<Node> open = new PriorityQueue<>(nodes.size(), (o1, o2) -> {
            double a = nodes.get(o1.getId()).getWeight();
            double b = nodes.get(o2.getId()).getWeight();
            double c = a - b;
            if (c < 0) return -1;
            if (c == 0) return 0;
            return 1;
        });
        Set<Node> closed = new HashSet<>();
        open.add(graph.getNodes().get(source));
        nodes.get(source).setWeight(0);
        while (!open.isEmpty()) {
            Node current = open.remove();
            open.remove(current);
            closed.add(current);
            DijkstraNode dijkstraCurrent = nodes.get(current.getId());
            for (Edge edge : current.getEdges()) {
                Node neighborNode = edge.getEnd(current);
                if (closed.contains(neighborNode)) {
                    continue;
                }
                open.add(neighborNode);
                DijkstraNode dijkstraNeighbor = nodes.get(neighborNode.getId());
                double alternativeWeight = dijkstraCurrent.getWeight() + edge.getPath().getWeight();
                double currentWeight = dijkstraNeighbor.getWeight();
                if (alternativeWeight < currentWeight) {
                    dijkstraNeighbor.setWeight(alternativeWeight);
                    dijkstraNeighbor.setPredecessor(dijkstraCurrent);
                }
            }
        }
    }

    public Path getShortestPath(Node target) {
        List<DijkstraNode> order = new ArrayList<>();
        DijkstraNode start = nodes.get(target.getId());
        order.add(start);
        DijkstraNode current = start;
        while (current.getPredecessor() != null) {
            current = current.getPredecessor();
            order.add(current);
        }
        double weight = start.getWeight();
        Collections.reverse(order);
        Node[] array = new Node[order.size()];
        for (int i = 0; i < order.size(); i++) {
            array[i] = graph.getNodes().get(order.get(i).getId());
        }
        return new Path(weight, array);
    }

    public double getWeight(Node target) {
        DijkstraNode start = nodes.get(target.getId());
        return start.getWeight();
    }


}

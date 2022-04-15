package com.franosch.bwinf.muellabfuhr.model;

import com.franosch.bwinf.muellabfuhr.model.graph.Cycle;
import com.franosch.bwinf.muellabfuhr.model.graph.Graph;
import com.franosch.bwinf.muellabfuhr.model.graph.Node;
import com.franosch.bwinf.muellabfuhr.model.graph.Path;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Runner {
    @Getter
    private final List<Cycle> cycles;
    private final Graph graph;

    @Getter
    private Path bridge;

    public Runner(Graph graph) {
        this.cycles = new ArrayList<>();
        this.graph = graph;
    }

    public double calcWeight() {
        double weight = sumCircles();
        bridge = calcBridge();
        if (bridge == null) return weight;
        weight += bridge.getWeight() * 2;
        return weight;
    }

    public Path calcFinalPath() {
        if (bridge == null) {
            bridge = calcBridge();
        }
        if (bridge == null) {
            return new Path(0, graph.getRoot());
        }
        List<Node> path = new ArrayList<>();
        if (bridge.getWeight() > 0) {
            if (bridge.getPath().length > 1) {
                path.addAll(Arrays.asList(Arrays.copyOfRange(bridge.getPath(), 0, bridge.getPath().length - 1)));
            }
        }

        Node current;
        if (path.size() == 0) {
            current = graph.getRoot();
        } else {
            current = bridge.getPath()[bridge.getPath().length - 1];
        }
        Cycle firstCycle = null;
        for (Cycle cycle : cycles) {
            if (cycle.isCircleNode(current)) {
                firstCycle = cycle;
                break;
            }
        }

        List<Cycle> copy = new CopyOnWriteArrayList<>(cycles);
        copy.remove(firstCycle);
        firstCycle.sortStartingWith(current);
        calcFinalPathRec(current, current, firstCycle, copy, path, true, 0);


        if (bridge.getWeight() > 0) {
            List<Node> reversedBridge = Arrays.asList(bridge.getPath());
            Collections.reverse(reversedBridge);
            path.addAll(reversedBridge);
        } else {
            path.add(graph.getRoot());
        }
        return new Path(calcWeight(), path.toArray(Node[]::new));
    }

    private void calcFinalPathRec(Node start, Node current,
                                  Cycle cycle, List<Cycle> open,
                                  List<Node> path, boolean cycleStart, int i) {
        if (!cycleStart && current.equals(start)) return;
        path.add(current);
        for (Cycle next : open) {
            if (next.isCircleNode(current)) {
                open.remove(next);
                next.sortStartingWith(current);
                Node currentNext = next.edges().get(0).getEnd(current);
                calcFinalPathRec(currentNext, currentNext, next, open, path, true, 1);
            }
        }
        i = i % cycle.edges().size();
        Node nextNode = cycle.edges().get(i).getEnd(current);
        i++;
        calcFinalPathRec(start, nextNode, cycle, open, path, false, i);

    }


    private Path calcBridge() {
        Set<Node> set = new HashSet<>();
        for (Cycle cycle : cycles) {
            set.addAll(cycle.getNodes());
        }
        if (set.contains(graph.getRoot())) {
            return new Path(0, graph.getRoot());
        }
        Node min = null;
        double local_weight = Double.MAX_VALUE;
        for (Node node : set) {
            double currentWeight = graph.getWeight(0, node.getId());
            if (currentWeight < local_weight) {
                local_weight = currentWeight;
                min = node;
            }
        }
        if (min == null) return null;
        return graph.getShortestPath(0, min.getId());
    }

    private double sumCircles() {
        double weight = 0;
        for (Cycle cycle : cycles) {
            weight += cycle.weight();
        }
        return weight;
    }

    @Override
    public String toString() {
        return "Runner{" +
                "weight=" + calcWeight() +
                ", circles=" + cycles +
                ", bridge=" + bridge +
                '}';
    }
}

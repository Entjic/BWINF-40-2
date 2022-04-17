package com.franosch.bwinf.muellabfuhr.model;

import com.franosch.bwinf.muellabfuhr.model.graph.*;
import com.franosch.bwinf.muellabfuhr.model.tuple.Pair;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class Runner {
    @Getter
    private final List<Cycle> cycles;
    private final Graph graph;

    @Getter
    @Setter
    private double bias;

    @Getter
    private Path bridge;

    public Runner(Graph graph) {
        this.cycles = new ArrayList<>();
        this.graph = graph;
    }

    public double calcWeight() {
        double weight = sumCycles();
        bridge = calcBridge();
        if (bridge == null) return weight;
        weight += bridge.getWeight() * 2;
        weight += bias * 2;
        return weight;
    }

    public Path getFinalPath() {
        Path path = calcFinalPath();
        Path result = new Path(calcWeightFromPath(path), path.getPath());
        return result;
    }


    private Path calcFinalPath() {
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
            if (cycle.isCycleNode(current)) {
                firstCycle = cycle;
                break;
            }
        }

        List<Cycle> copy = new ArrayList<>(cycles);

        List<Cycle> closedCycles = new ArrayList<>();
        copy.remove(firstCycle);
        closedCycles.add(firstCycle);
        firstCycle.sortStartingWith(current);
        calcFinalPathRec(current, current, firstCycle, copy, closedCycles, path, true, 0);
        copy.removeAll(closedCycles);
        while (!copy.isEmpty()) {
            Pair<Cycle, Path> pair = getShortestBridgeBetweenClusters(copy, closedCycles);
            Node start = pair.getRight().getTo();
            pair.getLeft().sortStartingWith(start);
            current = pair.getLeft().edges().get(0).getEnd(start);
            copy.remove(pair.getLeft());
            closedCycles.add(pair.getLeft());
            List<Node> generatedPath = new ArrayList<>();
            calcFinalPathRec(start, current, pair.getLeft(), copy, closedCycles, generatedPath, true, 1);
            copy.removeAll(closedCycles);
            List<Node> list = new ArrayList<>();
            List<Node> bridgePath = Arrays.asList(pair.getRight().getPath());
            list.addAll(bridgePath);
            list.addAll(generatedPath);
            List<Node> reversed = new ArrayList<>(bridgePath);
            Collections.reverse(reversed);
            list.addAll(reversed);
            int index = path.indexOf(pair.getRight().getFrom());
            path.addAll(index, list.subList(0, list.size() - 1));
        }

        if (bridge.getWeight() > 0) {
            List<Node> reversedBridge = Arrays.asList(bridge.getPath());
            Collections.reverse(reversedBridge);
            path.addAll(reversedBridge);
        } else {
            path.add(graph.getRoot());
        }
        return new Path(0, path.toArray(Node[]::new));
    }

    private double calcWeightFromPath(Path path) {
        Node[] pathPath = path.getPath();
        double weight = 0;
        for (int i = 0; i < pathPath.length - 1; i++) {
            Node node = pathPath[i];
            Node next = pathPath[i + 1];
            Edge edge = getEdge(node, next);
            weight += edge.getPath().getWeight();
        }
        return weight;
    }

    private Edge getEdge(Node from, Node to) {
        for (Edge edge : graph.getEdges()) {
            if (edge.getPath().getFrom().getId() == from.getId() || edge.getPath().getTo().getId() == from.getId()) {
                if (edge.getEnd(from).getId() == to.getId()) {
                    return edge;
                }
            }
        }
        return null;
    }


    private void calcFinalPathRec(Node start, Node current,
                                  Cycle cycle, List<Cycle> open,
                                  List<Cycle> closed,
                                  List<Node> path, boolean cycleStart, int i) {
        if (!cycleStart && current.equals(start)) return;
        path.add(current);
        StringBuilder stringBuilder = new StringBuilder();
        for (int j = 0; j < i; j++) {
            stringBuilder.append(" ");
        }
        // System.out.println(stringBuilder + "adding " + current + " from " + cycle.getId());

        for (Cycle next : open) {
            if (!closed.contains(next)) {
                if (next.isCycleNode(current)) {
                    //open.remove(next);
                    closed.add(next);
                    next.sortStartingWith(current);
                    Node currentNext = next.edges().get(0).getEnd(current);
                    // System.out.println("new recursion " + next.getId());
                    calcFinalPathRec(currentNext, currentNext, next, open, closed, path, true, 1);
                }
            }
        }
        i = i % cycle.edges().size();
        Node nextNode = cycle.edges().get(i).getEnd(current);
        // System.out.println("nextNode: " + nextNode + " from " + cycle.getId());
        i++;
        calcFinalPathRec(start, nextNode, cycle, open, closed, path, false, i);

    }

    private Pair<Cycle, Path> getShortestBridgeBetweenClusters(List<Cycle> open, List<Cycle> closed) {
        Cycle best = null;
        double weight = Double.MAX_VALUE;
        Path path = null;
        for (Cycle cycle : open) {
            for (Cycle allocated : closed) {
                for (Node node : allocated.getNodes()) {
                    for (Node cycleNode : cycle.getNodes()) {
                        DijkstraGraph dijkstraGraph = graph.getDijkstraGraph(node.getId());
                        double weightCurrent = dijkstraGraph.getWeight(cycleNode);
                        if (weightCurrent < weight) {
                            weight = weightCurrent;
                            best = cycle;
                            path = dijkstraGraph.getShortestPath(cycleNode);
                        }
                    }
                }
            }
        }
        return Pair.of(best, path);
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

    private double sumCycles() {
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
                ", bias=" + bias +
                ", circles=" + cycles +
                ", bridge=" + bridge +
                '}';
    }
}

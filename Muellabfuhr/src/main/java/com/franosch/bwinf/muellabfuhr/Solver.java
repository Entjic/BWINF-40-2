package com.franosch.bwinf.muellabfuhr;

import com.franosch.bwinf.muellabfuhr.io.FileReader;
import com.franosch.bwinf.muellabfuhr.model.graph.*;

import java.util.*;
import java.util.stream.Collectors;

public class Solver {
    private final Graph graph;

    public Solver() {
        this.graph = new Graph();
    }

    public void initGraph(FileReader fileReader) {
        graph.initGraph(fileReader);
    }

    public void makeEven() {
        System.out.println(graph);
        Set<Node> odds = findOddDegree();
        System.out.println("odds " + odds);
        if (odds.size() != 0) {
            Graph completeGraph = completeGraph(odds);
            System.out.println("graph completed");
            Set<Edge> min = findMinimalPerfectMatching(completeGraph);
            System.out.println("min" + min);
            double weight = 0;
            for (Edge edge : min) {
                weight += edge.getPath().getWeight();
            }
            System.out.println("weight " + weight);
            insert(min);
            System.out.println(graph);
        }
    }

    public List<Circle> solveChinesePostmanProblem() {
        List<Circle> cpp = eulerPath(graph);
        System.out.println(cpp);
        System.out.println(sum(cpp) / 5);

        return cpp;
    }


    private List<Circle> eulerPath(Graph graph) {
        List<Edge> open = new ArrayList<>(graph.getEdges());
        Node root = graph.getNodes().get(0);
        Node start;
        List<Circle> circles = new ArrayList<>();
        while (!open.isEmpty()) {
            start = findStart(circles, open);
            if (start == null) start = root;
            Node currentNode = start;
            List<Circle> circle = findCircle(currentNode, start, open);
            circles.addAll(circle);
        }
        int i = 0;
        for (Circle circle : circles) {
            System.out.println("Circle " + i + ": " + circle);
            i++;
        }
        return circles;
    }

    private List<Circle> findCircle(Node current, Node start, List<Edge> open) {
        List<Circle> out = new ArrayList<>();
        Edge edge;
        List<Edge> currentPath = new ArrayList<>();
        do {
            System.out.println(current.getId() + " " + start.getId());
            if (!current.equals(start) && isSubCircle(current, currentPath)) {
                Circle subCircle = getSubCircle(current, currentPath);
                System.out.println("sub circle " + subCircle);
                currentPath.removeAll(subCircle.edges());
                out.add(subCircle);
            }
            edge = getEdgeFromNodeAndOpen(current, open);
            current = edge.getEnd(current);
            currentPath.add(edge);
            open.remove(edge);
        }
        while ((!isCircle(start, currentPath) && !open.isEmpty()));
        Circle circle = new Circle(currentPath, currentPath.stream().mapToDouble(value -> value.getPath().getWeight()).sum());
        System.out.println("normal circle " + circle);
        out.add(circle);
        return out;
    }

    private boolean isSubCircle(Node current, List<Edge> path) {
        if (path.size() < 2) return false;
        List<Edge> list = new ArrayList<>(path);
        Collections.reverse(list);
        Node copy = current;
        for (Edge edge : list) {
            copy = edge.getEnd(copy);
            if (copy.equals(current)) return true;
        }
        return false;
    }

    private Circle getSubCircle(Node current, List<Edge> path) {
        List<Edge> list = new ArrayList<>(path);
        Collections.reverse(list);
        Node node = current;
        int i = 0;
        for (Edge e : list) {
            node = e.getEnd(node);
            if (node.equals(current)) {
                break;
            }
            i++;
        }
        List<Edge> copy = new ArrayList<>(path);
        List<Edge> out = copy.subList(copy.size() - i - 1, copy.size());
        Circle circle = new Circle(out, out.stream().mapToDouble(value -> value.getPath().getWeight()).sum());
        System.out.println(circle);
        return circle;
    }

    public Map<Integer, List<Circle>> allocate(int k, List<Circle> circles) {
        Map<Integer, List<Circle>> runner = new HashMap<>();
        for (int i = 0; i < k; i++) {
            runner.put(i, new ArrayList<>());
        }
        List<Circle> open = new ArrayList<>(circles);
        open.sort((o1, o2) -> {
            double weight1 = o1.weight();
            double weight2 = o2.weight();
            if (weight1 > weight2) return -1;
            if (weight1 < weight2) return 1;
            return 0;
        });
        allocateBiggest(runner.values(), open);

        Map<Integer, List<Circle>> map = compute(runner, open);
        List<Circle> biggest = getBiggestRunner(map.values());
        Map<Integer, List<Circle>> moreRunner = new HashMap<>();
        for (int i = 0; i < k; i++) {
            moreRunner.put(i, new ArrayList<>());
        }
        open = new ArrayList<>(circles);
        Circle biggestCircle = getBiggestCircle(biggest);
        List<Circle> next0 = new ArrayList<>(biggest);
        next0.remove(biggestCircle);
        Circle secondBiggestCircle = getBiggestCircle(next0);
        moreRunner.get(0).add(biggestCircle);
        open.remove(biggestCircle);
        moreRunner.get(1).add(secondBiggestCircle);
        open.remove(secondBiggestCircle);
        Collection<List<Circle>> runners = moreRunner.values().stream().filter(circles1 -> circles1.size() == 0).collect(Collectors.toList());
        allocateBiggest(runners, open);
        Map<Integer, List<Circle>> nextMap = compute(moreRunner, open);
        System.out.println(nextMap);

        // ---


        List<Circle> biggest0 = getBiggestRunner(nextMap.values());
        Map<Integer, List<Circle>> moreRunner0 = new HashMap<>();
        for (int i = 0; i < k; i++) {
            moreRunner0.put(i, new ArrayList<>());
        }
        open = new ArrayList<>(circles);
        Circle a0 = getBiggestCircle(biggest0);
        System.out.println(a0);
        System.out.println(biggest0.get(0));
        List<Circle> next = new ArrayList<>(biggest0);
        next.remove(a0);
        Circle b0 = getBiggestCircle(next);
        next.remove(b0);
        Set<Circle> circleSet = new HashSet<>();

        if (a0.equals(biggestCircle) || a0.equals(secondBiggestCircle)) {
            System.out.println("asdasd");
            a0 = getBiggestCircle(next);
            next.remove(a0);
        }
        if(b0.equals(biggestCircle) || b0.equals(secondBiggestCircle)){
            System.out.println("dsfgdfgdfg");
            b0 = getBiggestCircle(next);
            next.remove(b0);
        }
        circleSet.add(biggestCircle);
        circleSet.add(secondBiggestCircle);
        circleSet.add(a0);
        circleSet.add(b0);
        int i = 0;
        for (Circle circle : circleSet) {
            moreRunner0.get(i).add(circle);
            open.remove(circle);
            System.out.println("cool circle " + circle);
            i++;
        }
        Collection<List<Circle>> runners0 = moreRunner0.values().stream().filter(circles1 -> circles1.size() == 0).collect(Collectors.toList());
        allocateBiggest(runners0, open);
        Map<Integer, List<Circle>> nextMap0 = compute(moreRunner0, open);
        System.out.println(nextMap0);


        return nextMap;
    }

    private List<Circle> getBiggestRunner(Collection<List<Circle>> runners) {
        List<Circle> biggest = null;
        double weight = 0;
        for (List<Circle> runner : runners) {
            double currentWeight = sum(runner);
            if (currentWeight > weight) {
                weight = currentWeight;
                biggest = runner;
            }
        }
        return biggest;
    }

    private void allocateBiggest(Collection<List<Circle>> runners, List<Circle> open) {
        List<List<Circle>> copy = new ArrayList<>(runners);
        while (!copy.isEmpty()) {
            List<Circle> current = getLowest(runners);
            current.add(open.get(0));
            open.remove(0);
            copy.remove(current);
        }
    }

    private Circle getBiggestCircle(Collection<Circle> circles) {
        Circle biggest = null;
        double weight = 0;
        for (Circle circle : circles) {
            if (circle.weight() > weight) {
                biggest = circle;
                weight = circle.weight();
            }
        }
        return biggest;
    }

    private Map<Integer, List<Circle>> compute(Map<Integer, List<Circle>> runner, List<Circle> open) {
        while (!open.isEmpty()) {
            // System.out.println(open);
            Circle circle = handleCircle(runner, open);
            // System.out.println(circle);
            open.remove(circle);
        }
        for (Integer integer : runner.keySet()) {
            List<Circle> current = runner.get(integer);
            System.out.println("Runner " + integer + " got " + current.size() + " circles: weight " + sum(current) + " circles " + current);
        }
        StringBuilder stringBuilder = new StringBuilder();
        runner.keySet().forEach(integer -> stringBuilder.append("\n").append(sum(runner.get(integer))));
        System.out.println(stringBuilder);
        double weight = 0;
        for (List<Circle> value : runner.values()) {
            weight += sum(value);
        }
        System.out.println("Overall weight " + weight);
        for (Circle circle : open) {
            System.out.println("Still open circles " + circle);
        }
        return runner;
    }

    private List<Circle> getNeighbourCircle(Circle circle, List<Circle> circles) {
        List<Circle> out = new ArrayList<>();
        for (Circle current : circles) {
            if (isNeighbour(circle, current)) {
                out.add(current);
            }
        }
        return out;
    }

    private boolean isNeighbour(Circle current, Circle suspect) {
        for (Node node : current.getNodes()) {
            if (suspect.getNodes().contains(node)) return true;
        }
        return false;
    }

    private Circle handleCircle(Map<Integer, List<Circle>> runnerMap, List<Circle> open) {
        List<List<Circle>> openRunner = new ArrayList<>(runnerMap.values());
        while (!openRunner.isEmpty()) {
            List<Circle> runner = getLowest(openRunner);
            openRunner.remove(runner);
            List<Circle> list = new ArrayList<>(runner);
            Collections.reverse(list);
            for (Circle inner : list) {
                List<Circle> neighbouring = getNeighbourCircle(inner, open);
                if (!neighbouring.isEmpty()) {
                    // System.out.println("found smth");
                    Circle current = neighbouring.get(0);
                    runner.add(current);
                    return current;
                }
            }
        }
        return null;
    }

    private List<Circle> getLowest(Collection<List<Circle>> runner) {
        List<Circle> low = null;
        double weight = Double.MAX_VALUE;
        for (List<Circle> value : runner) {
            double currentWeight = sum(value);
            if (currentWeight < weight) {
                low = value;
                weight = currentWeight;
            }
        }
        return low;
    }

    private double sum(List<Circle> circles) {
        double weight = 0;
        for (Circle circle : circles) {
            weight += circle.weight();
        }
        return weight;
    }

    private Node findStart(List<Circle> circles, List<Edge> open) {
        for (Circle circle : circles) {
            for (Edge edge : circle.edges()) {
                Edge e = getEdgeFromNodeAndOpen(edge.getPath().getFrom(), open);
                if (e != null) return edge.getPath().getFrom();
                e = getEdgeFromNodeAndOpen(edge.getPath().getTo(), open);
                if (e != null) return edge.getPath().getTo();
            }
        }
        return null;
    }

    private boolean isCircle(Node start, List<Edge> edges) {
        Node current = start;
        for (Edge edge : edges) {
            current = edge.getEnd(current);
        }
        return current.equals(start);
    }

    private Edge getEdgeFromNodeAndOpen(Node current, List<Edge> open) {
        for (Edge edge : current.getEdges()) {
            if (open.contains(edge)) return edge;
        }
        return null;
    }

    private void insert(Set<Edge> min) {
        for (Edge edge : min) {
            for (int i = 0; i < edge.getPath().getPath().length - 1; i++) {
                Node a = edge.getPath().getPath()[i];
                Node b = edge.getPath().getPath()[i + 1];
                Node actualA = graph.getNodes().get(a.getId());
                Node actualB = graph.getNodes().get(b.getId());
                Edge edge1 = Edge.create(actualA, actualB, 0);
                Edge target = find(edge1);
                Edge copy = Edge.create(target.getPath(), target.getPath().getWeight());
                graph.connect(actualA, actualB, copy, false);
            }

        }
    }

    private Edge find(Edge edge) {
        for (Edge graphEdge : graph.getEdges()) {
            if (graphEdge.equals(edge)) return graphEdge;
        }
        return null;
    }

    private Graph completeGraph(Set<Node> odds) {
        Node root = odds.stream().findAny().orElse(null);
        Graph complete = new Graph(root);
        for (Node odd : odds) {
            Node node = new Node(odd.getId());
            complete.insert(node);
        }
        Set<Node> inners = new HashSet<>(odds);
        for (Node outer : odds) {
            inners.remove(outer);
            for (Node inner : inners) {
                Path path = this.graph.getShortestPath(outer.getId(), inner.getId());
                Edge edge = Edge.create(path, path.getWeight());
                // System.out.println("connecting " + edge);
                complete.connect(outer.getId(), inner.getId(), edge);
            }
        }
        return complete;
    }


    private Set<Edge> findMinimalPerfectMatching(Graph graph) {
        Node root = graph.getNodes().get(graph.getRoot().getId());
        Node current = root;
        Set<Integer> closed = new HashSet<>();
        List<Edge> path = new ArrayList<>();
        for (int i = 0; i < graph.getNodes().keySet().size() - 1; i++) {
            closed.add(current.getId());
            Set<Edge> open = getEdgesExcept(current.getId(), closed, graph.getNodes());
            Edge smallest = getSmallestNeighbour(open);
            path.add(smallest);
            current = smallest.getEnd(current);
        }
        Node finalCurrent = current;
        Edge endToRoot = root.getEdges().stream().filter(edge -> {
            Node end = edge.getEnd(root);
            Node node = graph.getNodes().get(end.getId());
            if (node == null) return false;
            return node.getId() == finalCurrent.getId();
        }).findAny().get();
        path.add(endToRoot);

        Set<Edge> out = pathToMinimalMatching(path);
        return out;
    }

    /**
     * Trivialer Algorithmus, abwechselnd in A und B einteilen, kleineres Matching wählen
     *
     * @param path
     * @return matching
     */
    private Set<Edge> pathToMinimalMatching(List<Edge> path) {
        Set<Edge> matchingA = new HashSet<>();
        Set<Edge> matchingB = new HashSet<>();
        int i = 0;
        for (Edge edge : path) {
            if (i == 0) {
                matchingA.add(edge);
                i++;
                continue;
            }
            matchingB.add(edge);
            i--;
        }
        double sumA = matchingA.stream().mapToDouble(value -> value.getPath().getWeight()).sum();
        double sumB = matchingB.stream().mapToDouble(value -> value.getPath().getWeight()).sum();
        if (sumA < sumB) return matchingA;
        return matchingB;
    }

    private Set<Edge> getEdgesExcept(int current, Set<Integer> closed, Map<Integer, Node> map) {
        Set<Edge> edges = new HashSet<>();
        Node node = map.get(current);

        for (Edge edge : node.getEdges()) {
            Node target = edge.getEnd(node);
            target = map.get(target.getId());
            if (target == null) continue;
            if (closed.contains(target.getId())) continue;
            edges.add(edge);
        }

        return edges;
    }

    private Edge getSmallestNeighbour(Set<Edge> edges) {
        double weight = Double.MAX_VALUE;
        Edge current = null;

        for (Edge neighbor : edges) {
            if (neighbor.getPath().getWeight() < weight) {
                current = neighbor;
                weight = neighbor.getPath().getWeight();
            }
        }
        return current;
    }


    public Set<Node> findOddDegree() {
        Set<Node> output = new HashSet<>();
        for (Node value : graph.getNodes().values()) {
            if (value.getDegree() % 2 == 0) continue;
            output.add(value);
        }
        return output;
    }
}

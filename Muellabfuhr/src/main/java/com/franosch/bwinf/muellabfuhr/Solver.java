package com.franosch.bwinf.muellabfuhr;

import com.franosch.bwinf.muellabfuhr.io.FileReader;
import com.franosch.bwinf.muellabfuhr.model.Runner;
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
            if (!current.equals(start) && isSubCircle(current, currentPath)) {
                Circle subCircle = getSubCircle(current, currentPath);
                currentPath.removeAll(subCircle.edges());
                out.add(subCircle);
            }
            edge = getEdgeFromNodeAndOpen(current, open);
            current = edge.getEnd(current);
            currentPath.add(edge);
            open.remove(edge);
        }
        while ((!isCircle(start, currentPath) && !open.isEmpty()));
        Circle circle = new Circle(currentPath);
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
        Circle circle = new Circle(out);
        return circle;
    }

    public Map<Integer, Runner> allocate(int k, List<Circle> circles) {
        Map<Integer, Runner> runner = new HashMap<>();
        for (int i = 0; i < k; i++) {
            runner.put(i, new Runner(graph));
        }

        circles.sort((o1, o2) -> {
            double weight1 = o1.weight();
            double weight2 = o2.weight();
            if (weight1 > weight2) return -1;
            if (weight1 < weight2) return 1;
            return 0;
        });
        List<Circle> open = new ArrayList<>(circles);
        allocateBiggest(runner.values(), open);

        Map<Integer, Runner> map = compute(runner, open);
        Runner biggest = getBiggestRunner(map.values());
        Map<Integer, Runner> moreRunner = new HashMap<>();
        for (int i = 0; i < k; i++) {
            moreRunner.put(i, new Runner(graph));
        }
        open = new ArrayList<>(circles);
        Circle biggestCircle = getBiggestCircle(biggest.getCircles());
        List<Circle> next0 = new ArrayList<>(biggest.getCircles());
        next0.remove(biggestCircle);
        Circle secondBiggestCircle = getBiggestCircle(next0);
        if (biggestCircle == null || secondBiggestCircle == null) {
            return map;
        }
        moreRunner.get(0).getCircles().add(biggestCircle);
        open.remove(biggestCircle);
        moreRunner.get(1).getCircles().add(secondBiggestCircle);
        open.remove(secondBiggestCircle);
        Collection<Runner> runners = moreRunner.values().stream().filter(r -> r.getCircles().size() == 0).collect(Collectors.toList());
        allocateBiggest(runners, open);
        Map<Integer, Runner> nextMap = compute(moreRunner, open);
        System.out.println(nextMap);

        // ---


        Runner biggest0 = getBiggestRunner(nextMap.values());
        Map<Integer, Runner> moreRunner0 = new HashMap<>();
        for (int i = 0; i < k; i++) {
            moreRunner0.put(i, new Runner(graph));
        }
        open = new ArrayList<>(circles);
        Circle a0 = getBiggestCircle(biggest0.getCircles());
        List<Circle> next = new ArrayList<>(biggest0.getCircles());
        next.remove(a0);
        Circle b0 = getBiggestCircle(next);
        next.remove(b0);
        Set<Circle> circleSet = new HashSet<>();

        if (a0.equals(biggestCircle) || a0.equals(secondBiggestCircle)) {
            a0 = getBiggestCircle(next);
            next.remove(a0);
        }
        if (b0.equals(biggestCircle) || b0.equals(secondBiggestCircle)) {
            b0 = getBiggestCircle(next);
            next.remove(b0);
        }
        circleSet.add(biggestCircle);
        circleSet.add(secondBiggestCircle);
        circleSet.add(a0);
        circleSet.add(b0);
        int i = 0;
        for (Circle circle : circleSet) {
            moreRunner0.get(i).getCircles().add(circle);
            open.remove(circle);
            System.out.println("cool circle " + circle);
            i++;
        }
        Collection<Runner> runners0 = moreRunner0.values().stream().filter(circles1 -> circles1.getCircles().size() == 0).collect(Collectors.toList());
        allocateBiggest(runners0, open);
        Map<Integer, Runner> nextMap0 = compute(moreRunner0, open);
        System.out.println(nextMap0);

        Runner biggestRunner = getBiggestRunner(nextMap0.values());
        if (nextMap.size() >= 5 && !nextMap.get(4).equals(biggestRunner)) {
            Map<Integer, Runner> muchMoreRunners = new HashMap<>();
            Circle hugeCircle = getBiggestCircle(biggestRunner.getCircles());
            List<Circle> copy = new ArrayList<>(biggestRunner.getCircles());
            copy.remove(hugeCircle);
            Circle second = getBiggestCircle(copy);
            for (int j = 0; j < k; j++) {
                muchMoreRunners.put(j, new Runner(graph));
            }
            open = new ArrayList<>(circles);
            open.remove(second);
            i = 0;
            for (Circle circle : circleSet) {
                muchMoreRunners.get(i).getCircles().add(circle);
                open.remove(circle);
                System.out.println("cool circle " + circle);
                i++;
            }
            muchMoreRunners.get(4).getCircles().add(second);
            nextMap = compute(muchMoreRunners, open);
            System.out.println(nextMap);
        }

        return nextMap;
    }

    private Runner getBiggestRunner(Collection<Runner> runners) {
        Runner biggest = null;
        double weight = 0;
        for (Runner runner : runners) {
            double currentWeight = runner.calcWeight();
            if (currentWeight > weight) {
                weight = currentWeight;
                biggest = runner;
            }
        }
        return biggest;
    }

    private void allocateBiggest(Collection<Runner> runners, List<Circle> open) {
        List<Runner> copy = new ArrayList<>(runners);
        while (!copy.isEmpty()) {
            Runner current = getLowest(runners);
            current.getCircles().add(open.get(0));
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

    private Map<Integer, Runner> compute(Map<Integer, Runner> runners, List<Circle> open) {
        while (!open.isEmpty()) {
            // System.out.println(open);
            Circle circle = handleCircle(runners, open);
            // System.out.println(circle);
            open.remove(circle);
        }
        for (Integer integer : runners.keySet()) {
            Runner current = runners.get(integer);
            System.out.println("Runner " + integer + " got " + current.getCircles().size()
                    + " circles: weight " + current.calcWeight() + " circles " + current.getCircles() + " bridge " + current.getBridge());
        }
        StringBuilder stringBuilder = new StringBuilder();
        runners.keySet().forEach(integer -> stringBuilder.append("\n").append(runners.get(integer).calcWeight()));
        System.out.println(stringBuilder);
        return runners;
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

    private Circle handleCircle(Map<Integer, Runner> runnerMap, List<Circle> open) {
        List<Runner> openRunner = new ArrayList<>(runnerMap.values());
        while (!openRunner.isEmpty()) {
            Runner runner = getLowest(openRunner);
            openRunner.remove(runner);
            List<Circle> list = new ArrayList<>(runner.getCircles());
            Collections.reverse(list);
            for (Circle inner : list) {
                List<Circle> neighbouring = getNeighbourCircle(inner, open);
                if (!neighbouring.isEmpty()) {
                    // System.out.println("found smth");
                    Circle current = neighbouring.get(0);
                    runner.getCircles().add(current);
                    return current;
                }
            }
        }
        return null;
    }
    private Runner getLowest(Collection<Runner> runner) {
        Runner low = null;
        double weight = Double.MAX_VALUE;
        for (Runner value : runner) {
            double currentWeight = value.calcWeight();
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
                Edge copy = Edge.create(target.getPath());
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
                Edge edge = Edge.create(path);
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

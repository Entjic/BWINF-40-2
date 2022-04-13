package com.franosch.bwinf.muellabfuhr;

import com.franosch.bwinf.muellabfuhr.io.FileReader;
import com.franosch.bwinf.muellabfuhr.model.graph.*;

import java.util.*;

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
        for (int i = 0; i < 5; i++) {
            List<Circle> lowest = getLowest(runner.values());
            Circle circle = open.get(0);
            lowest.add(circle);
            open.remove(circle);
        }
/*        for (int i = 2; i < 5; i++) {
            int r = new Random().nextInt(open.size());
            Circle circle = circles.get(r);
            List<Circle> lowest = getLowest(runner.values());
            lowest.add(circle);
            open.remove(circle);
        }*/

        System.out.println("1");
        Map<Integer, List<Circle>> map = compute(runner, open);
        Map<Integer, Double> weightMap = new HashMap<>();
        List<Circle> biggest = null;
        double weight = 0;
        for (Integer integer : map.keySet()) {
            List<Circle> r = map.get(integer);
            double w = sum(r);
            weightMap.put(integer, w);
            if (w > weight) {
                biggest = r;
                weight = w;
            }
        }
        Map<Integer, List<Circle>> moreRunner = new HashMap<>();
        for (int i = 0; i < k; i++) {
            moreRunner.put(i, new ArrayList<>());
        }
        open = new ArrayList<>(circles);
        Circle a = null;
        double moreWeight = 0;
        for (Circle circle : biggest) {
            if (circle.weight() > moreWeight) {
                a = circle;
                moreWeight = circle.weight();
            }
        }
        Circle b = null;
        double wayMoreWeight = 0;
        for (Circle circle : biggest.subList(1, biggest.size())) {
            if (circle.weight() > wayMoreWeight) {
                b = circle;
                wayMoreWeight = circle.weight();
            }
        }
        moreRunner.get(0).add(a);
        open.remove(a);
        moreRunner.get(1).add(b);
        open.remove(b);
        for (int i = 2; i < 5; i++) {
            Circle next = open.get(0);
            moreRunner.get(i).add(next);
            open.remove(0);
        }
        Map<Integer, List<Circle>> nextMap = compute(moreRunner, open);
        System.out.println(nextMap);

        // ---


        Map<Integer, Double> weightMap0 = new HashMap<>();
        List<Circle> biggest0 = null;
        double weight0 = 0;
        for (Integer integer : nextMap.keySet()) {
            List<Circle> r = nextMap.get(integer);
            double w = sum(r);
            weightMap0.put(integer, w);
            if (w > weight0) {
                biggest0 = r;
                weight0 = w;
            }
        }
        Map<Integer, List<Circle>> moreRunner0 = new HashMap<>();
        for (int i = 0; i < k; i++) {
            moreRunner0.put(i, new ArrayList<>());
        }
        open = new ArrayList<>(circles);
        Circle a0 = null;
        double moreWeight0 = 0;
        for (Circle circle : biggest0) {
            if (circle.weight() > moreWeight0) {
                a0 = circle;
                moreWeight0 = circle.weight();
            }
        }
        Circle b0 = null;
        double wayMoreWeight0 = 0;
        for (Circle circle : biggest0.subList(1, biggest0.size())) {
            if (circle.weight() > wayMoreWeight0) {
                b0 = circle;
                wayMoreWeight0 = circle.weight();
            }
        }
        moreRunner0.get(0).add(a);
        moreRunner0.get(1).add(b);
        moreRunner0.get(2).add(a0);
        moreRunner0.get(3).add(b0);
        open.remove(a);
        open.remove(b);
        open.remove(a0);
        open.remove(b0);
        for (int i = 4; i < 5; i++) {
            Circle next = open.get(0);
            moreRunner0.get(i).add(next);
            open.remove(0);
        }
        Map<Integer, List<Circle>> nextMap0 = compute(moreRunner0, open);
        System.out.println(nextMap0);


        return nextMap;
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

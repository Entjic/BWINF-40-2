package com.franosch.bwinf.muellabfuhr;

import com.franosch.bwinf.muellabfuhr.io.FileReader;
import com.franosch.bwinf.muellabfuhr.model.Result;
import com.franosch.bwinf.muellabfuhr.model.Runner;
import com.franosch.bwinf.muellabfuhr.model.graph.*;
import com.franosch.bwinf.muellabfuhr.model.tuple.Pair;

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
            genDijkstra();
        }
    }

    private void genDijkstra() {
        for (Node value : graph.getNodes().values()) {
            graph.getDijkstraGraph(value.getId());
        }
    }

    public List<Cycle> solveChinesePostmanProblem() {
        List<Cycle> cpp = eulerPath(graph);
        System.out.println(cpp);
        System.out.println(sum(cpp) / 5);

        return cpp;
    }


    private List<Cycle> eulerPath(Graph graph) {
        List<Edge> open = new ArrayList<>(graph.getEdges());
        Node root = graph.getNodes().get(0);
        Node start;
        List<Cycle> cycles = new ArrayList<>();
        while (!open.isEmpty()) {
            start = findStart(cycles, open);
            if (start == null) start = root;
            Node currentNode = start;
            List<Cycle> cycle = findCircle(currentNode, start, open);
            cycles.addAll(cycle);
        }
        int i = 0;
        for (Cycle cycle : cycles) {
            System.out.println("Circle " + i + ": " + cycle);
            i++;
        }
        return cycles;
    }

    private List<Cycle> findCircle(Node current, Node start, List<Edge> open) {
        List<Cycle> out = new ArrayList<>();
        Edge edge;
        List<Edge> currentPath = new ArrayList<>();
        do {
            if (!current.equals(start) && isSubCircle(current, currentPath)) {
                Cycle subCycle = getSubCircle(current, currentPath);
                currentPath.removeAll(subCycle.edges());
                out.add(subCycle);
            }
            edge = getEdgeFromNodeAndOpen(current, open);
            current = edge.getEnd(current);
            currentPath.add(edge);
            open.remove(edge);
        }
        while ((!isCircle(start, currentPath) && !open.isEmpty()));
        Cycle cycle = new Cycle(currentPath);
        out.add(cycle);
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

    private Cycle getSubCircle(Node current, List<Edge> path) {
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
        Cycle cycle = new Cycle(out);
        return cycle;
    }

    public List<Result> allocate(int k, List<Cycle> cycles) {
        Map<Integer, Runner> runner = new HashMap<>();
        for (int i = 0; i < k; i++) {
            runner.put(i, new Runner(graph));
        }
        List<Result> results = new ArrayList<>();
        cycles.sort((o1, o2) -> {
            double weight1 = o1.weight();
            double weight2 = o2.weight();
            if (weight1 > weight2) return -1;
            if (weight1 < weight2) return 1;
            return 0;
        });
        List<Cycle> open = new ArrayList<>(cycles);
        allocateBiggest(runner.values(), open);

        Map<Integer, Runner> map = compute(runner, open);
        results.add(new Result(map.values()));
        Runner biggest = getBiggestRunner(map.values());
        if (biggest.getCycles().size() < 2) return results;
        Map<Integer, Runner> moreRunner = new HashMap<>();
        for (int i = 0; i < k; i++) {
            moreRunner.put(i, new Runner(graph));
        }
        open = new ArrayList<>(cycles);
        Cycle biggestCycle = getBiggestCircle(biggest.getCycles());
        List<Cycle> next0 = new ArrayList<>(biggest.getCycles());
        next0.remove(biggestCycle);
        Cycle secondBiggestCycle = getBiggestCircle(next0);
        if (biggestCycle == null || secondBiggestCycle == null) {
            return results;
        }
        moreRunner.get(0).getCycles().add(biggestCycle);
        open.remove(biggestCycle);
        moreRunner.get(1).getCycles().add(secondBiggestCycle);
        open.remove(secondBiggestCycle);
        Collection<Runner> runners = moreRunner.values().stream().filter(r -> r.getCycles().size() == 0).collect(Collectors.toList());
        allocateBiggest(runners, open);
        Map<Integer, Runner> nextMap = compute(moreRunner, open);
        results.add(new Result(nextMap.values()));

        System.out.println(nextMap);

        // ---


        Runner biggest0 = getBiggestRunner(nextMap.values());
        Map<Integer, Runner> moreRunner0 = new HashMap<>();
        for (int i = 0; i < k; i++) {
            moreRunner0.put(i, new Runner(graph));
        }
        open = new ArrayList<>(cycles);
        Cycle a0 = getBiggestCircle(biggest0.getCycles());
        List<Cycle> next = new ArrayList<>(biggest0.getCycles());
        next.remove(a0);
        Cycle b0 = getBiggestCircle(next);
        next.remove(b0);
        Set<Cycle> cycleSet = new HashSet<>();

        if (a0.equals(biggestCycle) || a0.equals(secondBiggestCycle)) {
            a0 = getBiggestCircle(next);
            next.remove(a0);
        }
        if (b0.equals(biggestCycle) || b0.equals(secondBiggestCycle)) {
            b0 = getBiggestCircle(next);
            next.remove(b0);
        }
        cycleSet.add(biggestCycle);
        cycleSet.add(secondBiggestCycle);
        cycleSet.add(a0);
        cycleSet.add(b0);
        int i = 0;
        for (Cycle cycle : cycleSet) {
            moreRunner0.get(i).getCycles().add(cycle);
            open.remove(cycle);
            i++;
        }
        Collection<Runner> runners0 = moreRunner0.values().stream().filter(circles1 -> circles1.getCycles().size() == 0).collect(Collectors.toList());
        allocateBiggest(runners0, open);
        Map<Integer, Runner> nextMap0 = compute(moreRunner0, open);
        results.add(new Result(nextMap0.values()));
        System.out.println(nextMap0);

        Runner biggestRunner = getBiggestRunner(nextMap0.values());
        if (nextMap.size() >= 5 && !nextMap.get(4).equals(biggestRunner)) {
            Map<Integer, Runner> muchMoreRunners = new HashMap<>();
            Cycle hugeCycle = getBiggestCircle(biggestRunner.getCycles());
            List<Cycle> copy = new ArrayList<>(biggestRunner.getCycles());
            copy.remove(hugeCycle);
            Cycle second = getBiggestCircle(copy);
            for (int j = 0; j < k; j++) {
                muchMoreRunners.put(j, new Runner(graph));
            }
            open = new ArrayList<>(cycles);
            open.remove(second);
            i = 0;
            for (Cycle cycle : cycleSet) {
                muchMoreRunners.get(i).getCycles().add(cycle);
                open.remove(cycle);
                i++;
            }
            muchMoreRunners.get(4).getCycles().add(second);
            nextMap = compute(muchMoreRunners, open);
            results.add(new Result(nextMap.values()));
            System.out.println(nextMap);
        }
        return results;
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

    private void allocateBiggest(Collection<Runner> runners, List<Cycle> open) {
        List<Runner> copy = new ArrayList<>(runners);
        while (!copy.isEmpty() && !open.isEmpty()) {
            Runner current = getLowest(runners);
            current.getCycles().add(open.get(0));
            open.remove(0);
            copy.remove(current);
        }
    }

    private Cycle getBiggestCircle(Collection<Cycle> cycles) {
        Cycle biggest = null;
        double weight = 0;
        for (Cycle cycle : cycles) {
            if (cycle.weight() > weight) {
                biggest = cycle;
                weight = cycle.weight();
            }
        }
        return biggest;
    }

    private Map<Integer, Runner> compute(Map<Integer, Runner> runners, List<Cycle> open) {
        while (!open.isEmpty()) {
            // System.out.println(open);
            Cycle cycle = handleCircle(runners, open);
            // System.out.println(circle);
            open.remove(cycle);
        }
        for (Integer integer : runners.keySet()) {
            Runner current = runners.get(integer);
            System.out.println("Runner " + integer + " got " + current.getCycles().size()
                    + " circles: weight " + current.calcWeight() + " circles " + current.getCycles() + " bridge " + current.getBridge());
        }
        StringBuilder stringBuilder = new StringBuilder();
        runners.keySet().forEach(integer -> stringBuilder.append("\n").append(runners.get(integer).calcWeight()));
        System.out.println(stringBuilder);
        return runners;
    }

    private List<Cycle> getNeighbourCircle(Cycle cycle, List<Cycle> cycles) {
        List<Cycle> out = new ArrayList<>();
        for (Cycle current : cycles) {
            if (isNeighbour(cycle, current)) {
                out.add(current);
            }
        }
        return out;
    }

    private boolean isNeighbour(Cycle current, Cycle suspect) {
        for (Node node : current.getNodes()) {
            if (suspect.getNodes().contains(node)) return true;
        }
        return false;
    }

    private Cycle handleCircle(Map<Integer, Runner> runnerMap, List<Cycle> open) {
        Runner biggest = getBiggest(runnerMap.values());
        List<Runner> openRunner = new ArrayList<>(runnerMap.values());
        while (!openRunner.isEmpty()) {
            Runner runner = getLowest(openRunner);
            openRunner.remove(runner);
            List<Cycle> list = new ArrayList<>(runner.getCycles());
            Collections.reverse(list);
            for (Cycle inner : list) {
                List<Cycle> neighbouring = getNeighbourCircle(inner, open);
                if (!neighbouring.isEmpty()) {
                    // System.out.println("found smth");
                    Cycle current = neighbouring.get(0);
                    runner.getCycles().add(current);
                    return current;
                }
            }
            Pair<Cycle, Double> pair = getNonNeighbouringCycle(runner, open);
            double combined = runner.calcWeight() + pair.getLeft().weight() + 2 * pair.getRight();
            if (biggest.calcWeight() > combined) {
                runner.getCycles().add(pair.getLeft());
                runner.setBias(runner.getBias() + pair.getRight());
                return pair.getLeft();
            }
        }
        return null;
    }

    private Runner getBiggest(Collection<Runner> runners) {
        double weight = 0;
        Runner biggest = null;
        for (Runner runner : runners) {
            double currentWeight = runner.calcWeight();
            if (currentWeight > weight) {
                weight = currentWeight;
                biggest = runner;
            }
        }
        return biggest;
    }

    private Pair<Cycle, Double> getNonNeighbouringCycle(Runner runner, List<Cycle> open) {
        List<Cycle> list = new ArrayList<>(runner.getCycles());
        Collections.reverse(list);
        Cycle best = null;
        double weight = Double.MAX_VALUE;
        for (Cycle cycle : open) {
            for (Cycle allocated : list) {
                for (Node node : allocated.getNodes()) {
                    for (Node cycleNode : cycle.getNodes()) {
                        DijkstraGraph dijkstraGraph = graph.getDijkstraGraph(node.getId());
                        double weightCurrent = dijkstraGraph.getWeight(cycleNode);
                        if (weightCurrent < weight) {
                            weight = weightCurrent;
                            best = cycle;
                        }
                    }
                }
            }
        }
        return Pair.of(best, weight);
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

    private double sum(List<Cycle> cycles) {
        double weight = 0;
        for (Cycle cycle : cycles) {
            weight += cycle.weight();
        }
        return weight;
    }

    private Node findStart(List<Cycle> cycles, List<Edge> open) {
        for (Cycle cycle : cycles) {
            for (Edge edge : cycle.edges()) {
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

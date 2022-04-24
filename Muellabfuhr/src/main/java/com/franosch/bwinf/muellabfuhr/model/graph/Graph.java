package com.franosch.bwinf.muellabfuhr.model.graph;

import com.franosch.bwinf.muellabfuhr.io.FileReader;
import com.franosch.bwinf.muellabfuhr.io.InputParser;
import com.franosch.bwinf.muellabfuhr.io.InputString;
import lombok.Getter;

import java.util.*;
import java.util.function.Predicate;

public class Graph {

    @Getter
    private final Node root;
    @Getter
    private final List<Edge> edges;
    @Getter
    private final Map<Integer, Node> nodes;

    private Map<Integer, DijkstraGraph> dijkstraGraphs;

    public Graph(Node root) {
        this.root = root;
        this.nodes = new HashMap<>();
        this.edges = new ArrayList<>();
        this.dijkstraGraphs = new HashMap<>();
        insert(root);
    }

    public Graph() {
        this(new Node(0));
    }


    public void insert(Node node) {
        nodes.put(node.getId(), node);
    }

    public void connect(int a, int b, Edge edge) {
        Node nodeA = nodes.get(a);
        Node nodeB = nodes.get(b);
        connect(nodeA, nodeB, edge, true);
    }


    public void connect(Node a, Node b, Edge edge, boolean overRide) {
        if (!overRide) {
            connect(a, b, edge);
            edges.add(edge);
            return;
        }
        if (!edges.contains(edge)) {
            edges.add(edge);
            connect(a, b, edge);
        }
    }

    private void connect(Node a, Node b, Edge edge) {
        a.appendEdge(edge);
        b.appendEdge(edge);
    }


    public void initGraph(FileReader fileReader) {
        InputParser inputParser = new InputParser(fileReader);
        Map<String, Set<InputString>> input = inputParser.parse();
        Set<Node> open = new HashSet<>();

        open.add(root);
        while (open.size() != 0) {
            Node current = open.stream().findAny().get();
            buildGraph(current, input.getOrDefault(current.getId() + "", new HashSet<>()), open);
            open.remove(current);
        }

    }

    private void buildGraph(Node current, Set<InputString> inputStrings, Set<Node> open) {
        for (InputString string : inputStrings) {
            String to = (current.getId() + "").equals(string.to()) ? string.from() : string.to();
            int id = Integer.parseInt(to);
            Node node = nodes.get(id);
            if (node == null) {
                node = new Node(id);
                insert(node);
                // System.out.println("Created node: " + node);
                open.add(node);
            }
            Edge edge = Edge.create(current, node, Double.parseDouble(string.weight()));
            connect(current, node, edge, true);
        }
    }


    public DijkstraGraph getDijkstraGraph(Integer source) {
        if (!dijkstraGraphs.containsKey(source)) {
            DijkstraGraph dijkstraGraph = new DijkstraGraph(this, source);
            dijkstraGraph.generateShortestPaths();
            dijkstraGraphs.put(source, dijkstraGraph);
        }
        return dijkstraGraphs.get(source);
    }

    public Path getShortestPath(Integer source, Integer target) {
        DijkstraGraph dijkstraGraph = getDijkstraGraph(source);
        return dijkstraGraph.getShortestPath(nodes.get(target));
    }

    public double getWeight(Integer source, Integer target) {
        DijkstraGraph dijkstraGraph = getDijkstraGraph(source);
        return dijkstraGraph.getWeight(nodes.get(target));
    }


    public Node findById(int id) {
        Predicate<Node> predicate = node -> node.getId() == id;
        return getAny(predicate);
    }

    public Node getAny(Predicate<Node> predicate) {
        Set<Node> output = new HashSet<>();
        find(predicate, root, new HashSet<>(), output);
        return output.stream().findAny().orElse(null);
    }

    private void find(Predicate<Node> predicate, Node current, Set<Node> closed, Set<Node> output) {
        closed.add(current);
        if (predicate.test(current)) {
            output.add(current);
        }
        current.getEdges().stream()
                .map(edge -> edge.getEnd(current))
                .filter(node -> !closed.contains(node))
                .forEach(node -> find(predicate, node, closed, output));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        edges.forEach(edge -> stringBuilder
                .append("\n")
                .append(edge.getPath().getFrom())
                .append(" - ")
                .append(edge.getPath().getTo())
                .append(" ")
                .append(edge.getPath().getWeight()));
        return "Graph{" +
                "root=" + root +
                ", nodes=" + nodes.keySet() +
                ", edges=" + stringBuilder +
                '}';
    }

}

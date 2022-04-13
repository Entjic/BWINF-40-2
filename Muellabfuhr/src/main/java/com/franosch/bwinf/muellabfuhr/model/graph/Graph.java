package com.franosch.bwinf.muellabfuhr.model.graph;

import com.franosch.bwinf.muellabfuhr.io.FileReader;
import com.franosch.bwinf.muellabfuhr.io.InputParser;
import com.franosch.bwinf.muellabfuhr.io.InputString;
import lombok.Getter;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Graph {

    @Getter
    private final Node root;
    private final Map<DijkstraNode, Node> dijkstraNodeNodeMap;
    @Getter
    private final List<Edge> edges;
    @Getter
    private final Map<Integer, Node> nodes;

    public Graph(Node root) {
        this.root = root;
        this.nodes = new HashMap<>();
        this.dijkstraNodeNodeMap = new HashMap<>();
        this.edges = new ArrayList<>();
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

    public void generateShortestPaths(Node source) {
        List<Node> open = new ArrayList<>();
        Set<Node> closed = new HashSet<>();
        open.add(source);
        resetDijkstraNodes();
        source.getDijkstraNode().setWeight(0);
        while (!open.isEmpty()) {

            Node current = open.get(0);
            open.remove(current);
            closed.add(current);
            DijkstraNode dijkstraCurrent = current.getDijkstraNode();
            for (Edge edge : current.getEdges()) {
                Node neighborNode = edge.getEnd(current);
                if (closed.contains(neighborNode)) {
                    continue;
                }
                open.add(neighborNode);
                DijkstraNode dijkstraNeighbor = neighborNode.getDijkstraNode();
                double alternativeWeight = dijkstraCurrent.getWeight() + edge.getPath().getWeight();
                double currentWeight = dijkstraNeighbor.getWeight();
                if (alternativeWeight < currentWeight) {
                    dijkstraNeighbor.setWeight(alternativeWeight);
                    dijkstraNeighbor.setPredecessor(dijkstraCurrent);
                }
            }
            open.sort(Comparator.comparingDouble(o -> o.getDijkstraNode().getWeight()));
            Collections.reverse(open);
        }
    }

    public Path getShortestPath(Node target) {
        List<DijkstraNode> order = new ArrayList<>();
        order.add(target.getDijkstraNode());
        DijkstraNode current = target.getDijkstraNode();
        while (current.getPredecessor() != null) {
            current = current.getPredecessor();
            order.add(current);
        }
        double weight = target.getDijkstraNode().getWeight();
        Collections.reverse(order);
        Node[] array = new Node[order.size()];
        for (int i = 0; i < order.size(); i++) {
            array[i] = dijkstraNodeNodeMap.get(order.get(i));
        }
        return new Path(weight, array);
    }


    private void resetDijkstraNodes() {
        applyAll(node -> {
            node.setDijkstraNode(new DijkstraNode(node.getId()));
            dijkstraNodeNodeMap.put(node.getDijkstraNode(), node);
        });
    }

    public Node findById(int id) {
        Predicate<Node> predicate = node -> node.getId() == id;
        return getAny(predicate);
    }

    public Set<Node> findMultiple(Predicate<Node> predicate) {
        Set<Node> output = new HashSet<>();
        find(predicate, root, new HashSet<>(), output);
        return output;
    }

    public Node getAny(Predicate<Node> predicate) {
        Set<Node> output = new HashSet<>();
        find(predicate, root, new HashSet<>(), output);
        return output.stream().findAny().orElse(null);
    }

    public void applyAll(Consumer<Node> consumer) {
        apply(consumer, root, new HashSet<>());
    }

    private void apply(Consumer<Node> consumer, Node current, Set<Node> closed) {
        consumer.andThen(result -> {
            closed.add(result);
            result.getEdges().stream()
                    .map(neighbor -> neighbor.getEnd(result))
                    .filter(node -> !closed.contains(node))
                    .forEach(node -> apply(consumer, node, closed));
        }).accept(current);
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
                .append(" : ")
                .append(edge.getPath().getTo())
                .append(" weight ")
                .append(edge.getPath().getWeight()));
        return "Graph{" +
                "root=" + root +
                ", nodes=" + nodes.keySet() +
                ", edges=" + stringBuilder +
                '}';
    }

}

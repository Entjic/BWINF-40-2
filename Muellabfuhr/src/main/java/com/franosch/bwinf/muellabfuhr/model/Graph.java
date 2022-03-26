package com.franosch.bwinf.muellabfuhr.model;

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
    private final List<Edge> edges;
    private final Map<DijkstraNode, Node> nodes;

    public Graph(){
        this.root = new Node(0);
        this.edges = new ArrayList<>();
        this.nodes = new HashMap<>();
    }

    public Graph(Node root){
        this.root = root;
        this.edges = new ArrayList<>();
        this.nodes = new HashMap<>();
    }

    public void initGraph(FileReader fileReader){
        InputParser inputParser = new InputParser(fileReader);
        Map<String, Set<InputString>> input = inputParser.parse();
        Set<Node> closed = new HashSet<>();
        Set<Node> open = new HashSet<>();
        open.add(root);
        while (open.size() != 0){
            Node current = open.stream().findAny().get();
            buildGraph(current, input.getOrDefault(current.getId() + "", new HashSet<>()), open);
            closed.add(current);
            open.removeAll(closed);
        }

    }

    private void buildGraph(Node current, Set<InputString> inputStrings, Set<Node> open){
        for (InputString string : inputStrings) {
            String to = (current.getId() + "").equals(string.to()) ? string.from() : string.to();
            Node node = findById(Integer.parseInt(to));
            if(node == null){
                node = new Node(Integer.parseInt(to));
                // System.out.println("Created node: " + node);
                open.add(node);
            }
            Neighbor edge = Edge.create(current, node, Double.parseDouble(string.weight()));
            current.appendNeighbor(edge);
            node.appendNeighbor(edge);
        }
    }

    public void generateShortestPaths(Node source){
        List<Node> open = new ArrayList<>();
        Set<Node> closed = new HashSet<>();
        open.add(source);
        resetDijkstraNodes();
        source.getDijkstraNode().setWeight(0);
        while (!open.isEmpty()){
            Node current = open.get(0);
            open.remove(current);
            closed.add(current);
            DijkstraNode dijkstraCurrent = current.getDijkstraNode();
            for (Neighbor neighbor : current.getNeighbors()) {
                Node neighborNode = neighbor.getNeighbor(current);
                if(closed.contains(neighborNode)){
                    continue;
                }
                open.add(neighborNode);
                DijkstraNode dijkstraNeighbor = neighborNode.getDijkstraNode();
                double alternativeWeight = dijkstraCurrent.getWeight() + neighbor.getWeight();
                double currentWeight = dijkstraNeighbor.getWeight();
                if(alternativeWeight < currentWeight){
                    dijkstraNeighbor.setWeight(alternativeWeight);
                    dijkstraNeighbor.setPredecessor(dijkstraCurrent);
                }
            }
            open.sort(Comparator.comparingDouble(o -> o.getDijkstraNode().getWeight()));
            Collections.reverse(open);
        }
    }

    public Path getShortestPath(Node target){
        List<DijkstraNode> order = new ArrayList<>();
        order.add(target.getDijkstraNode());
        DijkstraNode current = target.getDijkstraNode();
        while (current.getPredecessor() != null){
            current = current.getPredecessor();
            order.add(current);
        }
        double weight = target.getDijkstraNode().getWeight();
        Collections.reverse(order);
        Node[] array = new Node[order.size()];
        for (int i = 0; i < order.size(); i++) {
            array[i] = nodes.get(order.get(i));
        }
        return new Path(weight, array);
    }


    private void resetDijkstraNodes(){
        applyAll(node ->{
            node.setDijkstraNode(new DijkstraNode(node.getId()));
            nodes.put(node.getDijkstraNode(), node);
        });
    }

    public Node findById(int id){
        Predicate<Node> predicate = node -> node.getId() == id;
        return findSingle(predicate);
    }

    public Set<Node> findMultiple(Predicate<Node> predicate){
        Set<Node> output = new HashSet<>();
        find(predicate, root, new HashSet<>(), output);
        return output;
    }

    public Node findSingle(Predicate<Node> predicate){
        Set<Node> output = new HashSet<>();
        find(predicate, root, new HashSet<>(), output);
        return output.stream().findAny().orElse(null);
    }

    public void applyAll(Consumer<Node> consumer){
        apply(consumer, root, new HashSet<>());
    }

    private void apply(Consumer<Node> consumer, Node current, Set<Node> closed){
        consumer.andThen(result -> {
            closed.add(result);
            result.getNeighbors().stream()
                    .map(neighbor -> neighbor.getNeighbor(result))
                    .filter(node -> !closed.contains(node))
                    .forEach(node -> apply(consumer, node, closed));
        }).accept(current);
    }

    private void find(Predicate<Node> predicate, Node current, Set<Node> closed, Set<Node> output){
        closed.add(current);
        if(predicate.test(current)){
            output.add(current);
        }
        current.getNeighbors().stream()
                .map(edge -> edge.getNeighbor(current))
                .filter(node -> !closed.contains(node))
                .forEach(node -> find(predicate, node, closed, output));
    }

}

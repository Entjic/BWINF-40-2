package com.franosch.bwinf.muellabfuhr;

import com.franosch.bwinf.muellabfuhr.io.FileReader;
import com.franosch.bwinf.muellabfuhr.io.InputParser;
import com.franosch.bwinf.muellabfuhr.io.InputString;
import com.franosch.bwinf.muellabfuhr.model.*;
import lombok.Getter;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.cycle.HierholzerEulerianCycle;
import org.jgrapht.alg.interfaces.EulerianCycleAlgorithm;
import org.jgrapht.alg.interfaces.ManyToManyShortestPathsAlgorithm;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.blossom.v5.KolmogorovWeightedPerfectMatching;
import org.jgrapht.alg.matching.blossom.v5.ObjectiveSense;
import org.jgrapht.alg.shortestpath.DijkstraManyToManyShortestPaths;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.util.SupplierUtil;

import java.util.*;

public class NextGenSolver {
    @Getter
    private final Graph<Node, DefaultWeightedEdge> graph;
    private Map<DijkstraNode, Node> nodes;

    public NextGenSolver(FileReader fileReader){

        this.graph = new DefaultUndirectedWeightedGraph<>(new NodeSupplier(), SupplierUtil.createDefaultWeightedEdgeSupplier());
        this.nodes = new HashMap<>();

        InputParser inputParser = new InputParser(fileReader);
        Map<String, Set<InputString>> map = inputParser.parse();
        Map<String, Node> nodes = new HashMap<>();
        for (String none : map.keySet()) {
            Node node = graph.addVertex();
            nodes.put(node.getId() + "", node);
        }
        for (Set<InputString> value : map.values()) {
            for (InputString inputString : value) {
                Node from = nodes.get(inputString.from());
                Node to = nodes.get(inputString.to());
                insert(from, to, Integer.parseInt(inputString.weight()));
            }
        }
    }

    private void insert(Node from, Node to, int weight){
        if(graph.containsEdge(from, to) || graph.containsEdge(to, from)){
            return;
        }
        DefaultWeightedEdge edge = graph.addEdge(from, to);
        graph.setEdgeWeight(edge, weight);
    }

    public Graph<Node, DefaultWeightedEdge> getOddGraph(){
        Graph<Node, DefaultWeightedEdge> odd = new DefaultUndirectedWeightedGraph<>(new NodeSupplier(), SupplierUtil.createDefaultWeightedEdgeSupplier());
        for (Node node : graph.vertexSet()) {
            if(graph.degreeOf(node) % 2 == 1){
                odd.addVertex(node);
            }
        }
        DijkstraManyToManyShortestPaths<Node, DefaultWeightedEdge> shortestPaths = new DijkstraManyToManyShortestPaths<>(graph);
        ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<Node, DefaultWeightedEdge> manyToManyShortestPaths = shortestPaths.getManyToManyPaths(odd.vertexSet(), odd.vertexSet());
        Set<Node> nodes = new HashSet<>(odd.vertexSet());
        for (Node node : odd.vertexSet()) {
            nodes.remove(node);
            for (Node inner : nodes) {
                DefaultWeightedEdge weightedEdge = odd.addEdge(node, inner);
                double weight = manyToManyShortestPaths.getWeight(node, inner);
                odd.setEdgeWeight(weightedEdge, weight);
               // System.out.println("init edge " + weightedEdge + " weight " + weight);
            }
        }
        return odd;
    }

    public Graph<Node, DefaultWeightedEdge> makeEuler(){
        Graph<Node, DefaultWeightedEdge> output = new CustomGraph<>(new NodeSupplier(), SupplierUtil.createDefaultWeightedEdgeSupplier());
        Graph<Node, DefaultWeightedEdge> odd = getOddGraph();
        MatchingAlgorithm.Matching<Node, DefaultWeightedEdge> perfect = perfectWeightMatching(odd);
        System.out.println("perfect " + perfect.getEdges());
        System.out.println("weight " + perfect.getWeight());
        merge(graph, output);
        for (DefaultWeightedEdge edge : perfect.getEdges()) {
            Node from = odd.getEdgeSource(edge);
            Node to = odd.getEdgeTarget(edge);
            output.addEdge(from, to, edge);
        }
        return output;
    }

    private void merge(Graph<Node, DefaultWeightedEdge> source, Graph<Node, DefaultWeightedEdge> target){
        for (Node node : source.vertexSet()) {
            target.addVertex(node);
        }
        for (DefaultWeightedEdge weightedEdge : source.edgeSet()) {
            Node from = source.getEdgeSource(weightedEdge);
            Node to = source.getEdgeTarget(weightedEdge);
            double weight = source.getEdgeWeight(weightedEdge);
            DefaultWeightedEdge inserted = target.addEdge(from, to);
            target.setEdgeWeight(inserted, weight);
        }
    }

    public MatchingAlgorithm.Matching<Node, DefaultWeightedEdge> perfectWeightMatching(Graph<Node, DefaultWeightedEdge> graph){
        KolmogorovWeightedPerfectMatching<Node, DefaultWeightedEdge> weightedPerfectMatching =
                new KolmogorovWeightedPerfectMatching<>(graph, ObjectiveSense.MINIMIZE);

        return weightedPerfectMatching.getMatching();
    }

    public GraphPath<Node, DefaultWeightedEdge> getEulerCycle(Graph<Node, DefaultWeightedEdge> graph){
        EulerianCycleAlgorithm<Node, DefaultWeightedEdge> eulerCycleAlgorithm = new HierholzerEulerianCycle<>();
        return eulerCycleAlgorithm.getEulerianCycle(graph);
    }


}

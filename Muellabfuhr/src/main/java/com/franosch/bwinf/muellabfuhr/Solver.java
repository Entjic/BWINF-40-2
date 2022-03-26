package com.franosch.bwinf.muellabfuhr;

import com.franosch.bwinf.muellabfuhr.io.FileReader;
import com.franosch.bwinf.muellabfuhr.model.Graph;
import com.franosch.bwinf.muellabfuhr.model.Node;
import com.franosch.bwinf.muellabfuhr.model.Path;

import java.util.*;

public class Solver {
    private final Graph graph;

    public Solver(){
        this.graph = new Graph();
    }

    public void initGraph(FileReader fileReader){
        graph.initGraph(fileReader);
    }

    public void makeEven(){
        Set<Node> odds = findOddDegree();
        Set<Path> completeGraph = completeGraph(odds);
        Set<Path> min = min(odds, completeGraph);
        System.out.println(min.size());
    }

    private Set<Path> completeGraph(Set<Node> odds){
        Set<Node> nodes = new HashSet<>(odds);
        Set<Path> paths = new HashSet<>();
        long a = System.currentTimeMillis();
        for (Node odd : odds) {
            nodes.remove(odd);
            graph.generateShortestPaths(odd);
            for (Node node : nodes) {
                Path path = graph.getShortestPath(node);
                // System.out.println(path);
                paths.add(path);
            }
        }
        System.out.println(odds);
        System.out.println("generated paths: " + paths.size());
        for (Node odd : odds) {
            System.out.println("generated paths for node " + odd);
            paths.stream().filter(path -> path.getTo().equals(odd) || path.getFrom().equals(odd)).forEach(System.out::println);
        }
        return paths;
    }

    private Set<Path> min(Set<Node> odds, Set<Path> paths){

        final Map<Integer, List<Path>> pathMap = new HashMap<>();
        paths.forEach(path -> {
            put(path, path.getFrom().getId(), pathMap);
            put(path, path.getTo().getId(), pathMap);
        });

        Stack<Node> open = new Stack<>();
        Set<Node> closed = new HashSet<>();
        Node current = odds.stream().findAny().get();
        open.add(current);
        while (!open.isEmpty()){
            current = open.pop();
            closed.add(current);
            List<Path> path = new ArrayList<>(pathMap.get(current.getId()));
            List<Path> openPaths = new ArrayList<>();
            for (Path next : path) {
                if(closed.contains(next.getFrom()) || closed.contains(next.getTo())){
                    continue;
                }
                openPaths.add(next);
            }

        }
        return new HashSet<>();
    }

    private Node getOther(Node current, Path path){
        return path.getFrom().equals(current) ? path.getTo() : path.getFrom();
    }

    private void put(Path path, int id, Map<Integer, List<Path>> map){
        List<Path> list = map.getOrDefault(id, new ArrayList<>());
        list.add(path);
        list.sort(Comparator.comparingDouble(Path::getWeight));
        map.put(id, list);
    }

    public Set<Node> findOddDegree(){
        return graph.findMultiple(node -> node.getDegree() % 2 == 1);
    }
}

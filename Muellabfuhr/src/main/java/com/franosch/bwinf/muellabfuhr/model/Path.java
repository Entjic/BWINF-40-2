package com.franosch.bwinf.muellabfuhr.model;

import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public class Path {
    private final Node from, to;
    private final Node[] path;
    private final double weight;

    public Path(double weight, Node... via){
        this.from = via[0];
        this.to = via[via.length - 1];
        this.path = via;
        this.weight = weight;
    }

    public Node getNext(Node current, Direction direction){
        for (int i = 0; i < path.length; i++) {
            if(!current.equals(path[i])){
                continue;
            }
            return path[i + direction.offSet];
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Path)) return false;
        Path path = (Path) o;
        return (from.equals(path.from) && to.equals(path.to)) || (to.equals(path.from) && from.equals(path.to));
    }

    @Override
    public int hashCode() {
        return Objects.hash(to) + Objects.hashCode(from);
    }

}

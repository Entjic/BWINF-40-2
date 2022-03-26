package com.franosch.bwinf.muellabfuhr.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class DijkstraNode {
    private DijkstraNode predecessor;
    private double weight = Double.MAX_VALUE;
    private final int id;
}

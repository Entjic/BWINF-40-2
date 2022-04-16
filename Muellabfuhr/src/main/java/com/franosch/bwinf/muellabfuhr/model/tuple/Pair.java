package com.franosch.bwinf.muellabfuhr.model.tuple;

import lombok.Data;

@Data(staticConstructor = "of")
public class Pair<A, B> {
    private final A left;
    private final B right;
}

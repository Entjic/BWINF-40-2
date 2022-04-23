package com.franosch.bwinf.zara.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
@Getter
public class Tuple<T> {
    private final T left, right;
    private final int size;
}

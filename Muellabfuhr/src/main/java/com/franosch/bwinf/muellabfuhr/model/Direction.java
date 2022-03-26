package com.franosch.bwinf.muellabfuhr.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Direction {
    ORIGINAL(1), REVERSED(-1);

    final int offSet;
}

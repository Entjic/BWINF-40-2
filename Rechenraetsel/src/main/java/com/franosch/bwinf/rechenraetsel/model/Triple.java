package com.franosch.bwinf.rechenraetsel.model;

import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;

public record Triple(Simplification previous, Simplification current, Simplification next) {
}

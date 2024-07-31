package com.example.wisetapiserver.domain;

import lombok.Getter;

@Getter
public class Coordinate {
    private int x;
    private int y;
    private double distance;

    public Coordinate(int x, int y, double distance) {
        this.x = x;
        this.y = y;
        this.distance = distance;
    }
}

package com.example.wisetapiserver.domain;

import lombok.Getter;

import java.util.List;
@Getter
public class ModelInput {
    private double azimuth;
    private double elevation;
    private List<Coordinate> coordinates;
    private double illum;
    private double x;
    private double y;

    public ModelInput(double azimuth, double elevation, List<Coordinate> coordinates, double illum, double x, double y) {
        this.azimuth = azimuth;
        this.elevation = elevation;
        this.coordinates = coordinates;
        this.illum = illum;
        this.x = x;
        this.y = y;
    }
}

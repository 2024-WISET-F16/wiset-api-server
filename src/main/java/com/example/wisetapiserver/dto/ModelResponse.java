package com.example.wisetapiserver.dto;

import java.util.List;

public class ModelResponse {
    private List<List<Double>> illum;

    // getters and setters
    public List<List<Double>> getIllum() {
        return illum;
    }

    public void setIllum(List<List<Double>> illum) {
        this.illum = illum;
    }
}
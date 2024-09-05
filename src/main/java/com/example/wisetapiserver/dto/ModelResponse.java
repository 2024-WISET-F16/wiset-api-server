package com.example.wisetapiserver.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ModelResponse {
    private List<List<Double>> illum;
    private Double avg;

}
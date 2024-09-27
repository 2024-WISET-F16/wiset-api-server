package com.example.wisetapiserver.controller;

import com.example.wisetapiserver.domain.Coordinate;
import com.example.wisetapiserver.domain.Illuminance;
import com.example.wisetapiserver.domain.ModelInput;
import com.example.wisetapiserver.dto.ModelResponse;
import com.example.wisetapiserver.service.illuminance.IlluminanceService;
import com.example.wisetapiserver.service.modeldata.ModelDataService;
import com.example.wisetapiserver.service.prediction.PredictionApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class IlluminanceController {

    private final IlluminanceService illuminanceService;
    private final ModelDataService modelDataService;
    private final PredictionApiService predictionApiService;


    @GetMapping("/grid")
    public Mono<ModelResponse> getIlluminanceMap(@RequestParam("x") int x, @RequestParam("y") int y) {
        ModelInput modelInput = modelDataService.ModelData(x, y);

        return predictionApiService.postModelServer(modelInput);
    }

    @GetMapping("/test")
    public Optional<Illuminance> getLatestData() {
        return illuminanceService.getLatestData();
    }


    @GetMapping("/analyze/avgIllum")
    public List<Double> getAvgIllum() {
        return illuminanceService.getAvgIllum();
    }

}

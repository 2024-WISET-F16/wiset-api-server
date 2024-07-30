package com.example.wisetapiserver.controller;

import com.example.wisetapiserver.domain.Illuminance;
import com.example.wisetapiserver.service.IlluminanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class IlluminanceController {

    private final IlluminanceService illuminanceService;

    @GetMapping("/grid")
    public String getIlluminanceMap(@RequestParam("x") int x, @RequestParam("y") int y) {

        return "test";
    }


    public Optional<Illuminance> getLatestData() {
        return illuminanceService.getLatestData();
    }
}

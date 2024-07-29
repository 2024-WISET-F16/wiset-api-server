package com.example.wisetapiserver.controller;

import com.example.wisetapiserver.domain.Illuminance;
import com.example.wisetapiserver.service.IlluminanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/illuminance")
public class IlluminanceController {
    private final IlluminanceService service;

    public IlluminanceController(IlluminanceService service) {
        this.service = service;
    }

    @GetMapping("/latest")
    public Illuminance getLatestIlluminance() {
        return service.getLatestIlluminance();
    }
}

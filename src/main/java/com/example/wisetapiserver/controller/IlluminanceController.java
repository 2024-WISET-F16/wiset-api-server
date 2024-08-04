package com.example.wisetapiserver.controller;

import com.example.wisetapiserver.domain.Illuminance;
import com.example.wisetapiserver.service.IlluminanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class IlluminanceController {
    private final IlluminanceService service;

    public IlluminanceController(IlluminanceService service) {
        this.service = service;
    }

    @GetMapping("/latest")
    public Illuminance getLatestIlluminance() {
        return service.getLatestIlluminance();
    }

    @GetMapping("/sun/riseAndSet")
    public Map<String, String> sunRiseAndSet() throws IOException, ParserConfigurationException, SAXException {

        String[] sunList = service.getSun();
        Map<String, String> response = new HashMap<>();
        response.put("sunrise", sunList[0]);
        response.put("sunset", sunList[1]);

        return response;
    }

}

package com.example.wisetapiserver.controller;

import com.example.wisetapiserver.domain.Illuminance;
import com.example.wisetapiserver.service.illuminance.IlluminanceService;
import com.example.wisetapiserver.service.sunposition.SunPosition;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class IlluminanceController {

    private final IlluminanceService illuminanceService;

    @GetMapping("/grid")
    public String getIlluminanceMap(@RequestParam("x") int x, @RequestParam("y") int y) {

        return "test";
    }

    @GetMapping("/sun-position")
    public double[] getSunPosition(@RequestParam String dateTime,
                                   @RequestParam double lat,
                                   @RequestParam double lng) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        return SunPosition.calculateAzEl(localDateTime, lat, lng);
    }

    @GetMapping("/test")
    public Optional<Illuminance> getLatestData() {
        return illuminanceService.getLatestData();
    }
}

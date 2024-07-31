package com.example.wisetapiserver.service.sunposition;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class SunPositionService {
    public double[] calculateAzEl(LocalDateTime dateTime) {
        return com.example.wisetapiserver.service.sunposition.SunPosition.calculateAzEl(dateTime);
    }
}
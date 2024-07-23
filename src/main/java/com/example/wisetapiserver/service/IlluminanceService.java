package com.example.wisetapiserver.service;

import com.example.wisetapiserver.domain.Illuminance;
import com.example.wisetapiserver.repository.IlluminanceRepository;
import org.springframework.stereotype.Service;

@Service
public class IlluminanceService {
    private final IlluminanceRepository repository;

    public IlluminanceService(IlluminanceRepository repository) {
        this.repository = repository;
    }

    public Illuminance getLatestIlluminance() {
        return repository.findTopByOrderByTimestampDesc();
    }
}

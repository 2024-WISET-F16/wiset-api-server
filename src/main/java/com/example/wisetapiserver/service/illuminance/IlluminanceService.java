package com.example.wisetapiserver.service.illuminance;

import com.example.wisetapiserver.domain.Illuminance;
import com.example.wisetapiserver.repository.IlluminanceRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IlluminanceService {

    private final IlluminanceRepository illuminanceRepository;

    public Optional<Illuminance> getLatestData() {
        return illuminanceRepository.findFirstByOrderByIdDesc();
    }
}

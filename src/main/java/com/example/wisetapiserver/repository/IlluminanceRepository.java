package com.example.wisetapiserver.repository;

import com.example.wisetapiserver.domain.Illuminance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IlluminanceRepository extends MongoRepository<Illuminance, String> {

    Optional<Illuminance> findFirstByOrderByIdDesc();
}


package com.example.wisetapiserver.repository;

import com.example.wisetapiserver.domain.Illuminance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IlluminanceRepository extends JpaRepository<Illuminance, Long> {

    Illuminance findTopByOrderByTimestampDesc();
}

package com.example.wisetapiserver.repository;

import com.example.wisetapiserver.domain.Illuminance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IlluminanceRepository extends JpaRepository<Illuminance, Long> {

    Illuminance findTopByOrderByTimestampDesc();

    @Query("SELECT AVG(i.value) FROM Illuminance i")
    Double findAverageValue();
}

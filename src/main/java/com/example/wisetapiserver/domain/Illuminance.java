package com.example.wisetapiserver.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Illuminance {
    @Id
    private long id;
    private double value;
    private LocalDateTime datetime;
}

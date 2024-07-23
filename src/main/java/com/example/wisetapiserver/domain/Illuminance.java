package com.example.wisetapiserver.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Illuminance {
    @Id
    private long id;
    private double value;
    private LocalDateTime timestamp;

}

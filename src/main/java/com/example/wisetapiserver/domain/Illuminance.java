package com.example.wisetapiserver.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;


@Document(collection = "illuminance")
@Getter
public class Illuminance {
    @Id
    private String id;
    private double illuminance;
    private LocalDateTime timestamp;

    @Builder
    public Illuminance(double illuminance, LocalDateTime timestamp) {
        this.illuminance = illuminance;
        this.timestamp = timestamp;
    }
}

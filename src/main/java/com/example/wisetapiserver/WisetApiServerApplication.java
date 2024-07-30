package com.example.wisetapiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class WisetApiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WisetApiServerApplication.class, args);
    }

}

package com.example.wisetapiserver.controller;

import com.example.wisetapiserver.domain.Illuminance;
import com.example.wisetapiserver.service.IlluminanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/illuminance")
public class IlluminanceController {
    private final IlluminanceService service;

    public IlluminanceController(IlluminanceService service) {
        this.service = service;
    }

    @GetMapping("/latest")
    public Illuminance getLatestIlluminance() {
        return service.getLatestIlluminance();
    }
}

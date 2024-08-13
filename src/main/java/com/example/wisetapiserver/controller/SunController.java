package com.example.wisetapiserver.controller;

import com.example.wisetapiserver.service.SunRiseSetService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SunController {
    // DI
    private final SunRiseSetService sunRiseSetService;
    public SunController(SunRiseSetService sunRiseSetService) {
        this.sunRiseSetService = sunRiseSetService;
    }

    @GetMapping("/sun/riseAndSet")
    public Map<String, String> sunRiseAndSet() throws IOException, ParserConfigurationException, SAXException {

        String[] sunList = sunRiseSetService.getSun();
        Map<String, String> response = new HashMap<>();
        response.put("sunrise", sunList[0]);
        response.put("sunset", sunList[1]);

        return response;
    }
}

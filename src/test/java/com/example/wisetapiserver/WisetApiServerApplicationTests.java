package com.example.wisetapiserver;

import com.example.wisetapiserver.service.IlluminanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@SpringBootTest
class WisetApiServerApplicationTests {

    @Autowired
    private IlluminanceService illuminanceService;

    void contextLoads() {
    }
    @Test
    void apiTest() throws IOException, ParserConfigurationException, SAXException {
        illuminanceService.getSun();
    }
}

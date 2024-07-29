package com.example.wisetapiserver.service;

import com.example.wisetapiserver.domain.Illuminance;
import com.example.wisetapiserver.repository.IlluminanceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

@Service
public class IlluminanceService {
    private final IlluminanceRepository repository;

    @Value("${servicekey}")
    private String serviceKey;

    public IlluminanceService(IlluminanceRepository repository) {
        this.repository = repository;
    }

    public Illuminance getLatestIlluminance() {
        return repository.findTopByOrderByTimestampDesc();
    }

    public String getSun() throws IOException, ParserConfigurationException,  SAXException {

        LocalDate date = LocalDate.now();
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = date.format(pattern); // 20241001

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B090041/openapi/service/RiseSetInfoService/getAreaRiseSetInfo");
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + serviceKey);
        urlBuilder.append("&" + URLEncoder.encode("locdate","UTF-8") + "=" + URLEncoder.encode(formattedDate, "UTF-8")); /*날짜*/
        urlBuilder.append("&" + URLEncoder.encode("location","UTF-8") + "=" + URLEncoder.encode("천안", "UTF-8")); /*지역*/

        URL url = new URL(urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        InputStream is = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder build = factory.newDocumentBuilder();
        Document doc = build.parse(is);

        Element element = doc.getDocumentElement();
        String sunrise = element.getElementsByTagName("sunrise").item(0).getTextContent();
        String sunset = element.getElementsByTagName("sunset").item(0).getTextContent();

        String formattedRisetime = formatTime(sunrise);
        String formattedSettime = formatTime(sunset);

        return "일출: " + formattedRisetime + " 일몰: " + formattedSettime;
    }
    public String formatTime(String time) {
        String hours = time.substring(0, 2);
        String minutes = time.substring(2, 4);

        return hours + ":" + minutes;
    }
}

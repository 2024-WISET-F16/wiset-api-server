package com.example.wisetapiserver.controller;

import com.example.wisetapiserver.domain.Coordinate;
import com.example.wisetapiserver.domain.Illuminance;
import com.example.wisetapiserver.domain.ModelInput;
import com.example.wisetapiserver.service.coordinate.CoordinateService;
import com.example.wisetapiserver.service.illuminance.IlluminanceService;
import com.example.wisetapiserver.service.sunposition.SunPosition;
import com.example.wisetapiserver.service.sunposition.SunPositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class IlluminanceController {

    private final IlluminanceService illuminanceService;
    private final SunPositionService sunPositionService;
    private final CoordinateService coordinateService;

    @GetMapping("/grid")
    public ModelInput getIlluminanceMap(@RequestParam("x") int x, @RequestParam("y") int y) {
        // MongoDB에서 최신 데이터를 가져옴
        Optional<Illuminance> latestDataOpt = illuminanceService.getLatestData();
        if (latestDataOpt.isEmpty()) {
            throw new RuntimeException("No data found");
        }

        // 최신 데이터에서 timestamp를 가져옴
        Illuminance latestData = latestDataOpt.get();
        LocalDateTime dateTime = latestData.getTimestamp();
        double illum = latestData.getIlluminance();

        // 해당 시간의 방위각과 고도각을 계산
        double[] azEl = sunPositionService.calculateAzEl(dateTime);

        // 좌표를 생성하고 각 좌표에서 (0, 0)과의 거리를 계산
        List<Coordinate> coordinates = coordinateService.generateCoordinates(x, y);

        // ModelInput 객체를 생성하여 반환
        return new ModelInput(azEl[0], azEl[1], coordinates, illum, x, y);
    }


    @GetMapping("/test")
    public Optional<Illuminance> getLatestData() {
        return illuminanceService.getLatestData();
    }
}

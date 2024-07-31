package com.example.wisetapiserver.service.coordinate;

import com.example.wisetapiserver.domain.Coordinate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CoordinateService {

    public List<Coordinate> generateCoordinates(int xCount, int yCount) {
        List<Coordinate> coordinates = new ArrayList<>();

        // x와 y의 간격을 계산
        int xInterval = 240 / (xCount - 1); // x 범위는 -120에서 120까지
        int yInterval = 180 / (yCount - 1); // y 범위는 0에서 180까지

        // 이중 루프를 통해 좌표 생성
        for (int i = 0; i < xCount; i++) {
            for (int j = 0; j < yCount; j++) {
                int x = -120 + i * xInterval; // x 좌표 계산
                int y = 0 + j * yInterval;    // y 좌표 계산
                double distance = calculateDistance(x, y); // (0, 0)과의 거리 계산
                coordinates.add(new Coordinate(x, y, distance)); // 좌표 객체를 생성하여 리스트에 추가
            }
        }
        return coordinates; // 좌표 리스트 반환
    }

    private double calculateDistance(int x, int y) {
        return Math.sqrt(x * x + y * y);
    }
}

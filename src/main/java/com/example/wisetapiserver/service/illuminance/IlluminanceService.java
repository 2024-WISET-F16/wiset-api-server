package com.example.wisetapiserver.service.illuminance;

import com.example.wisetapiserver.domain.Illuminance;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

@Service
@RequiredArgsConstructor
public class IlluminanceService {

    @Qualifier("illuminanceTemplate")
    private final MongoTemplate illuminanceTemplate;

    public Optional<Illuminance> getLatestData() {
        Query query = new Query(); //findFirstByOrderByIdDesc
        query.with(Sort.by(Sort.Direction.DESC, "_id"));  // _id 기준 내림차순 정렬
        return Optional.ofNullable(illuminanceTemplate.findOne(query, Illuminance.class));
    }

    public List<Double> getAvgIllum() {
        // 오전 5시부터 오후 7시 50분까지 10분 간격의 데이터를 배열에 넣음
        LocalDateTime startTime = LocalDate.now().atTime(5, 0);  // 오전 5시
        LocalDateTime endTime = LocalDate.now().atTime(19, 50);  // 오후 7시 50분


        // 9시간 빼서 UTC 시간으로 변환
        LocalDateTime utcStartTime = startTime.minusHours(9);  // UTC로 변환된 시작 시간
        LocalDateTime utcEndTime = endTime.minusHours(9);      // UTC로 변환된 종료 시간

        // UTC 시간대를 사용하여 Date로 변환
        Date startUtc = Date.from(utcStartTime.toInstant(ZoneOffset.UTC));
        Date endUtc = Date.from(utcEndTime.toInstant(ZoneOffset.UTC));
        // 90개의 데이터를 담을 배열 초기화 (0으로 채움)
        List<Double> illuminanceArray = new ArrayList<>(Collections.nCopies(90, 0.0));

        // MongoDB에서 오늘 날짜의 5시 ~ 19시 50분까지의 데이터 조회
        Query query = new Query(Criteria.where("timestamp").gte(startUtc).lte(endUtc));
        List<Illuminance> illuminanceDataList = illuminanceTemplate.find(query, Illuminance.class, "average_illuminance");


        for (int i = 0; i < 90; i++) {
            LocalDateTime currentTime = startTime.plusMinutes(i * 10);  // 10분 간격으로 시간 생성
            LocalDateTime nextTime = currentTime.plusMinutes(10);  // 다음 10분 후의 시간

            // 해당 시간대의 데이터를 찾음 (범위 비교)
            Illuminance data = illuminanceDataList.stream()
                    .filter(d -> {
                        // MongoDB에서 가져온 UTC 시간을 KST로 변환 후 9시간 더함
                        LocalDateTime timestamp = d.getTimestamp();
                        // 밀리초 무시하고 시간 범위로 비교
                        return !timestamp.isBefore(currentTime) && timestamp.isBefore(nextTime);
                    })
                    .findFirst()
                    .orElse(null);

            // 데이터가 있으면 배열에 넣고, 없으면 0 유지
            if (data != null) {
                illuminanceArray.set(i, data.getIlluminance());
            }
        }

        return illuminanceArray;

    }
}

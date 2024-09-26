package com.example.wisetapiserver.service.prediction;

import com.example.wisetapiserver.domain.Illuminance;
import com.example.wisetapiserver.domain.ModelInput;
import com.example.wisetapiserver.dto.ModelResponse;
import com.example.wisetapiserver.service.modeldata.ModelDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictionApiService {

    @Value("${model.server.uri}")
    private String url;
    private final ModelDataService modelDataService;

    @Qualifier("illuminanceTemplate")
    private final MongoTemplate illuminanceTemplate;

    public Mono<ModelResponse> postModelServer(ModelInput modelInput){

        // WebClient: 싱글 스레드, Non-Blocking 방식 사용
        WebClient webClient = WebClient.builder().build();

        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(modelInput), ModelInput.class)
                .retrieve()
                .bodyToMono(ModelResponse.class)
                .map(response -> {
                    // illum 값의 평균을 계산
                    Double avgIllum = calculateAverageIllum(response.getIllum());
                    // avgIllum 값을 ModelResponse 객체에 설정
                    response.setAvg(avgIllum);
                    return response;
                });
    }

    private Double calculateAverageIllum(List<List<Double>> illum) {
        if (illum == null || illum.isEmpty()) {
            return 0.0;
        }

        // 2차원 리스트를 1차원으로 펼치고 평균 계산
        double sum = illum.stream()
                .flatMap(List::stream)
                .mapToDouble(Double::doubleValue)
                .sum();
        long count = illum.stream()
                .flatMap(List::stream)
                .count();

        double average = (count > 0) ? sum / count : 0.0;

        // 소수점 둘째 자리까지 반올림
        return Math.round(average * 100.0) / 100.0;
    }

    // 스케줄러를 통해 10분마다 mongo db에 평균 조도 데이터 저장
    @Scheduled(initialDelay = 0, fixedRate = 600000)
    public void saveDataScheduler(){

        try {
            ModelInput modelInput = modelDataService.ModelData(3, 4);

            ModelResponse response = postModelServer(modelInput).block();
            Double avg = response.getAvg();

            Illuminance illuminance = Illuminance.builder()
                    .illuminance(avg)
                    .timestamp(LocalDateTime.now())
                    .build();

            illuminanceTemplate.insert(illuminance, "average_illuminance");

        } catch (Exception e){
            e.printStackTrace();
            System.out.println("MongoDB 저장 실패: " + e.getMessage());

        }
    }
}

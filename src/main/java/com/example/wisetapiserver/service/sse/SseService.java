package com.example.wisetapiserver.service.sse;

import com.example.wisetapiserver.domain.ModelInput;
import com.example.wisetapiserver.dto.GridDto;
import com.example.wisetapiserver.dto.ModelResponse;
import com.example.wisetapiserver.service.modeldata.ModelDataService;
import com.example.wisetapiserver.service.prediction.PredictionApiService;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // DI
    private final PredictionApiService predictionApiService;
    private final ModelDataService modelDataService;
    private final ObjectMapper objectMapper;
    private final GridDto gridDto;

    public SseService(PredictionApiService predictionApiService, ModelDataService modelDataService, ObjectMapper objectMapper, GridDto gridDto) {
        this.predictionApiService = predictionApiService;
        this.modelDataService = modelDataService;
        this.objectMapper = objectMapper;
        this.gridDto = gridDto;
    }

    public void addEmitter(SseEmitter emitter) {
        // 콜백함수 지정
        emitter.onTimeout(() -> emitter.complete()); // 타임아웃 발생
        emitter.onCompletion(() -> emitters.remove(emitter)); // 완료
        emitter.onTimeout(() -> emitters.remove(emitter)); // 에러 발생

        emitters.add(emitter); // 현재 emitter 저장

    }

    @Scheduled(fixedRate = 60000) // 60 * 1000 = 60,000 -> 1분 주기 스케줄러
    public void sendEvents() {

        for (SseEmitter emitter : emitters) {
            try {
                ModelInput modelInput = modelDataService.ModelData(gridDto.getX(), gridDto.getY()); // input data

                // 비동기적으로 모델 예측 결과를 가져와 처리
                predictionApiService.postModelServer(modelInput).subscribe(
                        modelResponse -> {
                            try {
                                // ModelResponse를 JSON으로 변환
                                String json = objectMapper.writeValueAsString(modelResponse);

                                // 클라이언트에 전송
                                emitter.send(json);
                                System.out.println("전송: " + json);
                            } catch (IOException e) {
                                e.printStackTrace();
                                emitter.complete();
                                emitters.remove(emitter);
                            }
                        },
                        error -> {
                            // 에러 처리
                            System.err.println("Error occurred: " + error.getMessage());
                            emitter.complete();
                            emitters.remove(emitter);
                        }
                );

            } catch (Exception e) {
                e.printStackTrace();
                emitter.complete();
                emitters.remove(emitter);
            }
        }
    }

    public void addGrid(int x, int y) {
        gridDto.setX(x);
        gridDto.setY(y);
    }
}

package com.example.wisetapiserver.service.sse;

import com.example.wisetapiserver.domain.ModelInput;
import com.example.wisetapiserver.dto.GridDto;
import com.example.wisetapiserver.service.modeldata.ModelDataService;
import com.example.wisetapiserver.service.prediction.PredictionApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {
    private final Map<SseEmitter, MongoClient> emitters = new ConcurrentHashMap<>();

    private final PredictionApiService predictionApiService;
    private final ModelDataService modelDataService;
    private final ObjectMapper objectMapper;
    private final GridDto gridDto;
    private final MongoClient mongoClient;


    public SseService(PredictionApiService predictionApiService, ModelDataService modelDataService,
                      ObjectMapper objectMapper, GridDto gridDto, MongoClient mongoClient) {
        this.predictionApiService = predictionApiService;
        this.modelDataService = modelDataService;
        this.objectMapper = objectMapper;
        this.gridDto = gridDto;
        this.mongoClient = mongoClient;

        // MongoDB Change Stream 구독 설정
        subscribeMongoChangeStream();
    }

    public void addEmitter(SseEmitter emitter) {
        emitter.onTimeout(() -> {
            emitter.complete();

            System.out.println("Emitter Timeout");
        });  // 타임아웃 발생 시

        emitter.onCompletion(() -> {
            emitters.remove(emitter); // emitters에서 emitter 제거
            closeMongoClient(emitter);  // MongoClient 연결 닫기

            System.out.println("Emitter Complete");
        });  // 완료 시

        emitter.onError((throwable) -> {
            System.out.println("Emitter Error: " + throwable.getMessage());

            emitter.complete();
            closeMongoClient(emitter);  // MongoClient 연결 닫기
        });  // 에러 발생 시

        emitters.put(emitter, mongoClient);  // 현재 emitter 저장
    }

    private void closeMongoClient(SseEmitter emitter) {
        MongoClient client = emitters.get(emitter);
        if (client != null) {
            client.close();  // MongoClient 연결 닫기
            emitters.remove(emitter);  // 관리 리스트에서 제거
        }
    }

    private void subscribeMongoChangeStream() {
        MongoDatabase database = mongoClient.getDatabase("illuminance"); // DB 이름 설정
        MongoCollection<Document> collection = database.getCollection("illuminance"); // 컬렉션 이름 설정

        new Thread(() -> { // MongoDB 컬렉션 변경사항 실시간 감지
            collection.watch()
                    .fullDocument(FullDocument.UPDATE_LOOKUP) // 문서 업데이트될 때 전체 문서 반환
                    .forEach(this::databaseChange); // 각 변경 사항 처리할 콜백 메서드 지정
        }).start();
    }

    private void databaseChange(ChangeStreamDocument<Document> event) {
        // MongoDB 변경 사항 감지될 때 실행
//        System.out.println("db 변경 감지: " + event.getFullDocument());

        try {
            ModelInput modelInput = modelDataService.ModelData(gridDto.getX(), gridDto.getY());

            // 비동기적으로 모델 예측 결과를 가져와 처리
            predictionApiService.postModelServer(modelInput).subscribe(
                    modelResponse -> {
                        try {
                            // illum 값의 평균을 계산하여 ModelResponse에 설정
                            Double avgIllum = calculateAverageIllum(modelResponse.getIllum());
                            modelResponse.setAvg(avgIllum); // 평균 값 설정

                            String json = objectMapper.writeValueAsString(modelResponse);

                            // 모든 SSE Emitter에 전송
                            for (SseEmitter emitter : emitters.keySet()) {
                                try {
                                    emitter.send(json);
                                } catch (IOException e) {
                                    emitter.complete();
                                    emitters.remove(emitter);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> System.err.println("error: " + error.getMessage())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Double calculateAverageIllum(List<List<Double>> illum) {
        if (illum == null || illum.isEmpty()) {
            return 0.0;
        }

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

    public void addGrid(int x, int y) {
        gridDto.setX(x);
        gridDto.setY(y);
    }
}

package com.example.wisetapiserver.service.sse;

import com.example.wisetapiserver.service.prediction.PredictionApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {

    @Autowired
    private PredictionApiService predictionApiService;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public void addEmitter(SseEmitter emitter) {
        // 콜백함수 지정
        emitter.onTimeout(() -> emitter.complete()); // 타임아웃 발생
        emitter.onCompletion(() -> emitters.remove(emitter)); // 완료
        emitter.onTimeout(() -> emitters.remove(emitter)); // 에러 발생

        emitters.add(emitter); // 현재 emitter 저장

    }

    @Scheduled(fixedRate = 100000)
    public void sendEvents() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send("Hello, World!");
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(emitter);
            }
        }
    }
}

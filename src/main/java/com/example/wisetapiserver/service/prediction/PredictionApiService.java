package com.example.wisetapiserver.service.prediction;

import com.example.wisetapiserver.domain.ModelInput;
import com.example.wisetapiserver.dto.ModelResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PredictionApiService {
    public Mono<ModelResponse> postModelServer(ModelInput modelInput){

        // WebClient: 싱글 스레드, Non-Blocking 방식 사용
        WebClient webClient = WebClient.builder().build();
        String url = "http://127.0.0.1:8000/model-data";

        return webClient.post()
                .uri(url) // URL 정의
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(modelInput), ModelInput.class)
                .retrieve()
                .bodyToMono(ModelResponse.class);
    }
}

package com.example.wisetapiserver.controller;

import com.example.wisetapiserver.service.sse.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
public class SseController {

    @Autowired
    private SseService sseService;

    @GetMapping(value = "/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sseEmitterExample() throws Exception{
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE); // 파라미터로 timeout 시간
        sseService.addEmitter(sseEmitter);
        sseService.sendEvents();

        return sseEmitter; // 메세지가 발생할 때마다 클라이언트에 반환

    }


}

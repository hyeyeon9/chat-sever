package com.example.chatserver.common.sse;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {
    private final Map<String, SseEmitter> emitters  =new ConcurrentHashMap<>();

    public SseEmitter subscribe(String email){
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitters.put(email, emitter);

        emitter.onCompletion(() -> emitters.remove(email));
        emitter.onTimeout(() -> emitters.remove(email));

        System.out.println(email + " SSE 구독 연결됨!!!!");
        return emitter;
    }

    public void sendUnreadCount(String email, Object data){
        System.out.println("sendUnreadCount 호출!!");
        System.out.println(email);
        SseEmitter emitter = emitters.get(email);
        System.out.println(emitter);

        if(emitter != null){
            try{
                emitter.send(SseEmitter.event()
                        .name("unread-count")
                        .data(data)
                );
                System.out.println("메시지 전송!!");
            }catch (Exception e){
                emitters.remove(email);
            }
        }
    }
}

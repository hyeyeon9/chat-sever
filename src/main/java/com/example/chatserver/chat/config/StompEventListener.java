package com.example.chatserver.chat.config;

// 이벤트를 처리하는 목적이 아니라 어떤 이벤트가 발생했는지만 캐치하는 목적
// 스프링과 stomp는 기본적으로 세션관리를 자동(내부적)으로 처리
// 연결 및 해제 이벤트를 기록, 연결된 세션수를 실시간으로 확인할 목적으로 이벤트 리스너를 생성 => 로그, 디버깅 목적

import io.lettuce.core.event.connection.ConnectEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class StompEventListener {
    private final Set<String> sessions = ConcurrentHashMap.newKeySet();

    @EventListener
    public void connectHandler(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        sessions.add(accessor.getSessionId());
        log.info("Connected: {}", accessor.getSessionId());
        log.info("Total sessions: {}", sessions.size());
    }

    @EventListener
    public void disconnectHandler(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        sessions.remove(accessor.getSessionId());
        log.info("DisConnected: {}", accessor.getSessionId());
        log.info("Total sessions: {}", sessions.size());
    }

}

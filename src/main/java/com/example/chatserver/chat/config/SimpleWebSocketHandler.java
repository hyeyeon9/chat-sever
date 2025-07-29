//package com.example.chatserver.chat.config;
//
//import lombok.extern.log4j.Log4j2;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.*;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
////  /connect로 웹소켓 연결요청이 들어왔을때 이를 처리할 클래스
//@Component
//@Log4j2
//public class SimpleWebSocketHandler extends TextWebSocketHandler {
//
//    // 그냥 HashSet은 스레드 세이프 하지 않기에(여러 요청이 몰리면 다 저장X될 확률 잇음), 스레드 세이프한 아래 SET을 사용핧 것
//    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
//
//    // 연결된 이후 SET자료구조에 클라이언트 정보 저장
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        sessions.add(session);
//        log.info("연결됨 세션 ID: {}", session.getId());
//    }
//
//    // 사용자한테 메시지를 보내주는 역할
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        String payload = message.getPayload();
//        log.info("받은 메시지: {}", payload);
//        for(WebSocketSession s: sessions){
//            if(s.isOpen()){
//                // sessions에 저장된 사용자 모두에게 메시지를 보낼 것
//                s.sendMessage(new TextMessage(payload));
//            }
//        }
//
//    }
//
//
//    // 연결이 끊기면 세션을 삭제
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        sessions.remove(session);
//        log.info("연결 종료");
//    }
//
//
//}

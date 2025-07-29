//package com.example.chatserver.chat.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//
//// build.gradle에 websocket관려 의존성을 설치했기에 아래 어노테이션이나 상속받을 수 있는 것
//@Configuration
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketConfigurer {
//    private final  SimpleWebSocketHandler simpleWebSocketHandler;
//
//    public WebSocketConfig(SimpleWebSocketHandler simpleWebSocketHandler) {
//        this.simpleWebSocketHandler = simpleWebSocketHandler;
//    }
//
//    // 웹소켓 코드를 처리할 핸들러를 정의하고, 이 안에 처리내용을 작성
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        //  /connect url로 websocket연결 요청이 들어오면, 핸들러 클래스가 처리
//        registry.addHandler(simpleWebSocketHandler, "/connect")
//                // securityconfig에서의 cors예외는 http요청에 대한 예외다.
//                // 따라서 웹소켓 프로토콜에 대한 요청에 대해서는 별도의 cors 설정이 필요하다.
//                .setAllowedOrigins("http://localhost:3000");
//    }
//}
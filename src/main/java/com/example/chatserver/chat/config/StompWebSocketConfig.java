package com.example.chatserver.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private  final StompHandler stompHandler;

    public StompWebSocketConfig(StompHandler stompHandler) {
        this.stompHandler = stompHandler;
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect")
                .setAllowedOrigins("http://localhost:3000")
                // ws://가 아니라 http:// 엔드포인트를 사용할 수 있게 해주는 sockJS 라이브러리를 통한 요쳥을 허용하는 설정
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지를 발행을 할때는 /publish 하고 => /publish/1 이런식으로 메시지를 발행해야함
        // /publish로 시작하는 url패턴으로 메시지가 발행되면 @Controller 객체의
        // @MessageMapping 메서드로 라우팅된다.
        registry.setApplicationDestinationPrefixes("/publish");

        // /topic/1 형태로 메시지를 수신(subscribe)해야 함을 설
        registry.enableSimpleBroker("/topic");
    }

    // 웹소켓요청(connect, subscribe, disconnect)등의 요청시에는 http header등 http 메시지를 넣어올 수 있고,
    // 이를 인터셉터를 통해 가로채 토큰을 검증할 수 있음
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 사용자 -> filter를 거쳐서 -> Config로 들어와서 연결을 하기 전에 가로챌 것
        // 로그인 했는지 확이해야 하니까 (토큰이 있는지 없는지)


        registration.interceptors(stompHandler);
    }
}

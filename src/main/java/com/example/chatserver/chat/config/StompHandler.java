package com.example.chatserver.chat.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class StompHandler implements ChannelInterceptor {
    // 토큰 꺼내서 검증하는 작업
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);



        if(StompCommand.CONNECT == accessor.getCommand()){
            log.info("connect 요청시 토큰 유효성 검증");
            String bearerToken =  accessor.getFirstNativeHeader("Authorization");
            String token = bearerToken.substring(7);

            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.info("토큰 검증 완료");

        }

        return message;
    }

}

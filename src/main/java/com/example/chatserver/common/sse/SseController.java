package com.example.chatserver.common.sse;


import com.example.chatserver.common.auth.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@RequestMapping("sse")
public class SseController {

    @Value("${jwt.secretKey}")
    private String secretKey;

    private final SseService sseService;

    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    // MediaType.TEXT_EVENT_STREAM_VALUE : SSE를 위한 HTTP 응답 타입 설정 이다.
    // 이걸 설정하면 프론트가 sse 스트림이라고 인식할 수 있다.
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam("token") String token){

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.getSubject();
        System.out.println(email + " SSE 구독 연결됨");
        return sseService.subscribe(email);
    }
}

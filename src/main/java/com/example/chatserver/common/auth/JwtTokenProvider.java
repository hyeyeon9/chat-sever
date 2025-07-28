package com.example.chatserver.common.auth;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String secretKey;

    private final int expiration;

    private  Key SECRET_KEY;

    public JwtTokenProvider(@Value("${jwt.secretKey}") String secretKey,
                            @Value("${jwt.expiration}") int expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
        // 인코딩된 시크릿키를 다시 decode하고, 동시에 HS512 알고리즘을 통해서 시크릿 키를 암호화
        this.SECRET_KEY = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey),
                SignatureAlgorithm.HS512.getJcaName());
    }

    // 토큰 생성하기
    public String createToken(String email, String role){
        Claims claims = Jwts.claims().setSubject(email); // 페이로더 개념, subject는 페이로더의 키 값으로 들어갈 것 => 이메일
        claims.put("role", role);
        Date now = new Date();

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 발행시간
                .setExpiration(new Date(now.getTime()+expiration*60*1000L)) // 만료일자, 현재시간 + 만료일시
                .signWith(SECRET_KEY) // 서명
                .compact();
        return token;
    }
}

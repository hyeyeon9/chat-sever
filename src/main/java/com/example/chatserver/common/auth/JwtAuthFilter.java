package com.example.chatserver.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 사용자 요청이 들어오면 token을 까봐서 우리가 만들어준 토큰인지 아닌지 여기서 확인하는 코드를 아래에 작성할 것

@Component
public class JwtAuthFilter extends GenericFilter {

    private final HttpServletResponse httpServletResponse;
    @Value("${jwt.secretKey}")
    private String secretKey;

    public JwtAuthFilter(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }


    @Override
    // request 안에 토큰이 위치함, 토큰을 검증해보고 잘못됐으면 res에 에러를 반환해주고,
    // 정상이면 doFilter로 이동하자.
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String token = httpServletRequest.getHeader("Authorization");
        try{
            if(token != null){

                // Bearer 형식 ?
                if(!token.substring(0,7).equals("Bearer ")){
                    throw new AuthenticationServiceException("Bearer 형식이 아닙니다. ");
                }
                String jwtToken = token.substring(7);
                // 토큰의 시그니처 부분을 검증해야 한다.
                // claims는 페이로더 부분임, 가져오는 과정에서 검증이 되고, claims를 가져와서 Authentication 객체를 만들 것
                Claims claims =  Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(jwtToken) // 여기까지가 시크릿키를 가지고 토큰을 다시 만들어본 것 => 이 과정에서 자동으로 검증이 됨 , parserBuilder()가 해줌
                        .getBody();

                // Authentication 객체 생성
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority( "ROLE_" + claims.get("role"))); // 규칙적으로 "ROLE_"을 앞에 붙이고, 뒤에 우리가 지정한 어드민/유저 가 들어갈 것

                UserDetails userDetails = new User(claims.getSubject(), "",authorities ); // 이메일, 비밀번호, 권한 3개의 매개변수가 들어감, 권한은 위에서 만듦

                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,"", userDetails.getAuthorities()); // user정보관련 매개변수 3개 들어가야함
                SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContextHolder안에 getContext꺼내고, 그 안에 authentication 있음


            }

            filterChain.doFilter(request, response);
        }catch (Exception e){
            e.printStackTrace();
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().write("Invalid token");
        }

    }
}

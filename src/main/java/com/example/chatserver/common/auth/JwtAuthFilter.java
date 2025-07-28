package com.example.chatserver.common.auth;

import jakarta.servlet.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 사용자 요청이 들어오면 token을 까봐서 우리가 만들어준 토큰인지 아닌지 여기서 확인하는 코드를 아래에 작성할 것

@Component
public class JwtAuthFilter extends GenericFilter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
     filterChain.doFilter(request, response);
    }
}

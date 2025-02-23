package com.bside.mzoffice.chat.interceptor;

import com.bside.mzoffice.common.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    public WebSocketAuthInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
//            String token = httpRequest.getHeader("Authorization");

            // chrome web socket client 에서 테스트 하기 위한 코드
            String token = httpRequest.getParameter("token");

            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7);
                try {
                    Claims claims = jwtService.parseToken(jwt);
                    Long customerId = Long.valueOf(claims.getSubject()); // id 추출
                    attributes.put("customerId", customerId);
                } catch (Exception e) {
                    return false; // 토큰이 유효하지 않으면 연결 거부
                }
            } else {
                return false; // 토큰이 없으면 연결 거부
            }
        }
        return true; // 인증 성공
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {

    }
}


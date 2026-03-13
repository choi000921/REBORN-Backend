// /security/JwtAuthenticationFilter.java
package com.example.kmjoonggo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// OncePerRequestFilter: 모든 요청(Request)마다 '한 번만' 실행되는 필터
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. HTTP Request Header에서 토큰 추출
        String token = extractToken(request);

        // 2. 토큰 유효성 검사 (validateToken)
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // 3. 토큰이 유효하면, 토큰에서 Authentication 객체를 가져옴
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // 4. (핵심) SecurityContextHolder에 이 인증 정보를 저장
            // -> 이 요청은 '인증된 사용자'의 요청임을 명시
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    // Request Header에서 "Authorization" 필드의 "Bearer " 토큰을 추출
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 토큰 값
        }
        return null;
    }
}
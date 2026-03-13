// /security/JwtTokenProvider.java
package com.example.kmjoonggo.security;

import com.example.kmjoonggo.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key secretKey;
    private final long expirationMilliseconds;
    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey,
                            @Value("${jwt.expiration-ms}") long expirationMilliseconds,
                            UserDetailsService userDetailsService) {
        // application.properties의 secret-key를 Base64 디코딩하여 Key 객체로 변환
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        this.expirationMilliseconds = expirationMilliseconds;
        this.userDetailsService = userDetailsService;
    }

    // 1. (로그인 성공 시) JWT 토큰 생성
    public String createToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMilliseconds);

        return Jwts.builder()
                .setSubject(user.getUsername()) // (중요) user.getEmail()
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // 2. (API 요청 시) HTTP 헤더에서 토큰을 읽어와 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        // (중요) DB에서 email로 유저 정보를 다시 로드
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 3. 토큰에서 이메일(Subject) 추출
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // 4. 토큰 유효성 검증 (만료, 서명 등)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // (로그) e.g., SignatureException, ExpiredJwtException...
            return false;
        }
    }
}
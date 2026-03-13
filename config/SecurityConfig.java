// /config/SecurityConfig.java
package com.example.kmjoonggo.config;

import com.example.kmjoonggo.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// CORS 관련
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Configuration
    public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry
                    .addResourceHandler("/images/**")
                    .addResourceLocations("file:/C:/upload/");
        }
    }

    // 비밀번호 인코더 (개발용: NoOp)
    @Bean
    public PasswordEncoder passwordEncoder() {
        // return new BCryptPasswordEncoder(); // 실제 배포시 권장
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // ✅ CORS 설정 (WebConfig 대신 여기서 처리)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 리액트 개발 서버만 허용
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF 비활성화 (JWT 사용 시)
                .csrf(csrf -> csrf.disable())

                // 세션을 상태 없이 사용 (JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 경로별 인가 설정
                .authorizeHttpRequests(authorize -> authorize
                        // 프리플라이트 요청 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 인증 없이 접근 가능한 AUTH API
                        .requestMatchers("/api/auth/**").permitAll()

                        // ✅ 공개 상품 API들 (로그인 불필요)
                        .requestMatchers(HttpMethod.GET,
                                "/api/products",              // 메인 목록
                                "/api/products/top-views",    // 인기 상품 Top5
                                "/api/products/search",       // 검색
                                "/api/products/{productId}"   // 상세보기 (PathVariable 패턴)
                        ).permitAll()

                        // 혹시 예전에 쓰던 /api/search 도 열어둘거면
                        .requestMatchers(HttpMethod.GET, "/api/search").permitAll()

                        // 이 외의 /api/** 는 로그인 필요 (my-sales, my-purchases, upload 등)
                        .requestMatchers("/api/**").authenticated()

                        // 그 외 리소스(정적 파일 등)는 모두 허용
                        .anyRequest().permitAll()
                );

        // JWT 필터 추가
        http.addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

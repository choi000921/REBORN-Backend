
/*
// /config/WebConfig.java
package com.example.kmjoonggo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 1. 모든 경로(/api/**, /admin/** 등)에 대해
                .allowedOrigins("http://localhost:3000") // 2. http://localhost:3000 (React)의 요청을 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 3. 허용할 HTTP 메소드
                .allowedHeaders("*") // 4. 모든 헤더 (Authorization 포함) 허용
                .allowCredentials(true); // 5. (선택) 쿠키/세션 인증이 필요하면 true
    }
}
*/

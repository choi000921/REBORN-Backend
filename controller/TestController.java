package com.example.kmjoonggo.controller; // (본인 패키지 경로에 맞게 수정)

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // 이 파일이 웹 API 컨트롤러임을 선언
public class TestController {

    @GetMapping("/api/hello") // http://localhost:8080/api/hello 요청을 처리
    public String hello() {
        return "Hello Kmjoonggo Connection Success!";
    }
}
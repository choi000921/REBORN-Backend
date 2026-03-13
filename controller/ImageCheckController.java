// /controller/ImageCheckController.java
package com.example.kmjoonggo.controller;

import com.example.kmjoonggo.dto.ImageCheckResponse;
import com.example.kmjoonggo.service.ImageCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/products") // (참고) /api/products 하위에 배치
@RequiredArgsConstructor
public class ImageCheckController {

    private final ImageCheckService imageCheckService;

    /**
     * (핵심) 이미지 위험도 검사 API
     * React가 'imageFile'이라는 Key로 이미지를 보내면 @RequestParam이 받습니다.
     */
    @PostMapping("/check-image")
    public ResponseEntity<ImageCheckResponse> checkImage(
            @RequestParam("imageFile") MultipartFile imageFile
    ) {
        // (보안) 로그인한 사용자만 이 API를 호출할 수 있도록
        // Spring Security가 자동으로 검사할 것입니다.

        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ImageCheckResponse("Error", "이미지 파일이 비어있습니다."));
        }

        try {
            ImageCheckResponse response = imageCheckService.checkImageRisk(imageFile);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new ImageCheckResponse("Error", "이미지 분석 중 서버 오류가 발생했습니다."));
        }
    }
}
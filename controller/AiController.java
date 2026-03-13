// /controller/AiController.java
package com.example.kmjoonggo.controller;

import com.example.kmjoonggo.dto.AiCompareRequest;
import com.example.kmjoonggo.dto.AiCompareResponse;
import com.example.kmjoonggo.service.AiCompareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiCompareService aiCompareService;

    @PostMapping("/compare")
    // (수정) 반환 타입을 AiCompareResponse로 변경
    public ResponseEntity<AiCompareResponse> getAiComparison(
            @RequestBody AiCompareRequest request
    ) {
        try {
            // (수정) Service가 DTO를 직접 반환
            AiCompareResponse responseDto = aiCompareService.getAiComparison(request);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            e.printStackTrace();
            // (수정) 오류 발생 시에도 DTO 형식으로 반환 (선택 사항)
            AiCompareResponse errorResponse = new AiCompareResponse(
                    0, // scoreA (int)
                    0, // scoreB (int)
                    "error", // recommendation (String)
                    "AI 비교 중 오류가 발생했습니다.", // reason (String)
                    "오류", // prosA (String)
                    "오류", // consA (String)
                    "오류", // prosB (String)
                    "오류"  // consB (String)
            );
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
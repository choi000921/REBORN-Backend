// /dto/AiAnalyzeResponse.java
package com.example.kmjoonggo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자
public class AiAnalyzeResponse {

    private String productName;     // AI가 생성한 제품명
    private String category;        // AI가 추천한 카테고리
    private String productDescription; // AI가 생성한 제품 설명
}
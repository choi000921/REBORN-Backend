// /dto/AiCompareResponse.java
package com.example.kmjoonggo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor; // (추가) JSON 파싱을 위해 기본 생성자 추가

@Getter
@NoArgsConstructor // (추가)
@AllArgsConstructor
public class AiCompareResponse {
    // 3번(시각화) + 4번(점수)
    private int scoreA; // 상품 A의 최종 점수 (0-100)
    private int scoreB; // 상품 B의 최종 점수 (0-100)

    private String recommendation; // "productA" 또는 "productB"
    private String reason;         // "코피왕님, A를 추천합니다..."
    private String prosA;
    private String consA;
    private String prosB;
    private String consB;
}
// /dto/ImageCheckResponse.java
package com.example.kmjoonggo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자
public class ImageCheckResponse {

    private String riskLevel; // "High", "Medium", "Low"
    private String reason;    // "다른 쇼핑몰에서 동일한 이미지가 발견되었습니다."
}
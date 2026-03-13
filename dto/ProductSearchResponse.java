package com.example.kmjoonggo.dto;

import com.example.kmjoonggo.domain.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductSearchResponse {

    private Long productId;
    private String productName;
    private int price;
    private String productImage1;
    private int views;
    private int ribbons;

    private String category;          // 예: "디지털기기"
    private ProductStatus status;     // FOR_SALE / RESERVED / SOLD

    private String productLocation1;  // 시/도
    private String productLocation2;  // 구/시
    private String productLocation3;  // 동/읍/면

    private LocalDateTime postedDate;
}

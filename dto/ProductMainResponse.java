// src/main/java/com/example/kmjoonggo/dto/ProductMainResponse.java
package com.example.kmjoonggo.dto;

import com.example.kmjoonggo.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductMainResponse {

    private Long productId;
    private String productName;
    private Integer price;
    private String productImage1;
    private Integer views;

    // 🔥 메인에서도 시간 계산 가능하게
    private LocalDateTime postedDate;

}

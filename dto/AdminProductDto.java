// /dto/AdminProductDto.java
package com.example.kmjoonggo.dto;

import com.example.kmjoonggo.domain.Product;
import com.example.kmjoonggo.domain.ProductStatus;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class AdminProductDto {
    // AdminProductList.js가 요구하는 필드
    private Long productId;
    private String userId; // (판매자 ID)
    private String productName;
    private LocalDateTime postedDate;
    private ProductStatus status;

    // --- (이 생성자가 향후 발생할 오류를 방지합니다) ---
    // Product 엔티티를 AdminProductDto로 변환하는 생성자
    public AdminProductDto(Product product) {
        this.productId = product.getProductId();
        this.userId = product.getSeller().getUserId();
        this.productName = product.getProductName();
        this.postedDate = product.getPostedDate();
        this.status = product.getStatus();
    }
    // --- (여기까지) ---
}
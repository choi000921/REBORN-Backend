// /dto/MySalesListResponse.java
package com.example.kmjoonggo.dto;

import com.example.kmjoonggo.domain.Product;
import com.example.kmjoonggo.domain.ProductStatus; // (ProductStatus Enum 경로)
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class MySalesListResponse {

    private Long productId;
    private String productName;
    private LocalDateTime postedDate;
    private ProductStatus status;
    private int views;
    private int ribbons; // 찜 수

    public MySalesListResponse(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.postedDate = product.getPostedDate();
        this.status = product.getStatus();
        this.views = product.getViews();
        // (수정) Product 엔티티의 ribbons (List<Ribbon>)의 size()를 가져옴
        this.ribbons = product.getRibbons().size();
    }
}
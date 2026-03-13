// /dto/MyRecentViewResponse.java
package com.example.kmjoonggo.dto;

import com.example.kmjoonggo.domain.Product;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class MyRecentViewResponse {

    private Long productId;
    private String productName;
    private int price;
    private String imageUrl1; // 대표 이미지
    private LocalDateTime postedDate;
    private int views;
    private int ribbons;

    // Product 엔티티를 이 DTO로 변환하는 생성자
    public MyRecentViewResponse(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.price = product.getPrice();
        this.imageUrl1 = product.getProductImage1(); // 첫 번째 이미지만 가져옴
        this.postedDate = product.getPostedDate();
        this.views = product.getViews();
        this.ribbons = product.getRibbons().size(); // 이 상품의 총 찜 개수
    }
}
// /dto/MainPageProductDto.java
package com.example.kmjoonggo.dto;

import com.example.kmjoonggo.domain.Product;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class MainPageProductDto {
    // SearchProductCard.js가 요구하는 필드
    private Long id; // (productId)
    private String name; // (productName)
    private int price;
    private String imageUrl; // (productImage1)
    private LocalDateTime postedDate;
    private int views;
    private int ribbons;

    // Product 엔티티를 이 DTO로 변환하는 생성자
    public MainPageProductDto(Product product) {
        this.id = product.getProductId();
        this.name = product.getProductName();
        this.price = product.getPrice();
        this.imageUrl = product.getProductImage1();
        this.postedDate = product.getPostedDate();
        this.views = product.getViews();
        this.ribbons = product.getRibbons().size(); // 찜 수
    }
}
// /dto/MyPurchasesResponse.java
package com.example.kmjoonggo.dto;

import com.example.kmjoonggo.domain.Product;
import lombok.Getter;

@Getter
public class MyPurchasesResponse {

    private Long productId;
    private String productName;
    private int price;

    // Product 엔티티를 이 DTO로 변환하는 생성자
    public MyPurchasesResponse(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.price = product.getPrice();
    }
}
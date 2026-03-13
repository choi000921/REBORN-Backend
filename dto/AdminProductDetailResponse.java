// /dto/AdminProductDetailResponse.java
package com.example.kmjoonggo.dto;

import com.example.kmjoonggo.domain.Product;
import com.example.kmjoonggo.domain.ProductStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AdminProductDetailResponse {

    // 기획안의 모든 필드
    private Long productId;
    private String sellerId; // (기획안의 'userID')
    private String productName;
    private ProductStatus status;
    private String productDescription;
    private String category;
    private int price;
    private LocalDateTime postedDate;
    private int ribbons; // 찜 수
    private int views;

    // 위치 (기획안대로 A, B, C로 재조합)
    private String productLocationA;
    private String productLocationB;
    private String productLocationC;

    // 이미지
    private String productImage1;
    private String productImage2;
    private String productImage3;


    // Product 엔티티를 DTO로 변환하는 생성자
    public AdminProductDetailResponse(Product product) {
        this.productId = product.getProductId();
        this.sellerId = product.getSeller().getUserId(); // User 엔티티에서 ID 가져오기
        this.productName = product.getProductName();
        this.status = product.getStatus();
        this.productDescription = product.getProductDescription();
        this.category = product.getCategory();
        this.price = product.getPrice();
        this.postedDate = product.getPostedDate();
        this.views = product.getViews();
        this.ribbons = product.getRibbons().size(); // List<Ribbon>의 크기 계산

        // (기획안) Location 1~9를 3개씩 합침
        this.productLocationA = product.getProductLocation1() + " " + product.getProductLocation2() + " " + product.getProductLocation3();
        this.productLocationB = product.getProductLocation4() + " " + product.getProductLocation5() + " " + product.getProductLocation6();
        this.productLocationC = product.getProductLocation7() + " " + product.getProductLocation8() + " " + product.getProductLocation9();

        // 이미지 경로
        this.productImage1 = product.getProductImage1();
        this.productImage2 = product.getProductImage2();
        this.productImage3 = product.getProductImage3();
    }
}
package com.example.kmjoonggo.dto;

import com.example.kmjoonggo.domain.Product;
import com.example.kmjoonggo.domain.ProductStatus;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ProductDetailResponse {

    private Long id;                // 상품 PK
    private String sellerId;        // 판매자 ID (User.userId)
    private String name;            // 상품명
    private int price;              // 가격
    private String description;     // 설명

    private List<String> images;    // 이미지 URL 리스트
    private String status;          // "판매 중" / "예약 중" / "판매 완료"
    private int ribbons;            // 리본(찜) 개수
    private int views;              // 조회수
    private String category;        // 카테고리

    private List<String> locations; // "서울특별시 강남구 역삼동" 같은 한 줄짜리들
    private String postedDate;      // 등록일 문자열
    private String userNickname;    // 판매자 닉네임

    // ⭐ 추가: 현재 로그인 유저가 이 상품을 찜했는지 여부
    private boolean liked;

    // 기본 생성자: liked=false 로 처리
    public ProductDetailResponse(Product p) {
        this(p, false);
    }

    // liked까지 전달받는 생성자
    public ProductDetailResponse(Product p, boolean liked) {
        this.liked = liked;

        this.id = p.getProductId();
        this.sellerId = p.getSeller().getUserId();
        this.name = p.getProductName();
        this.price = p.getPrice();
        this.description = p.getProductDescription();
        this.views = p.getViews();
        this.category = p.getCategory();
        this.userNickname = p.getSeller().getUserNickname();

        // 날짜 포맷
        if (p.getPostedDate() != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            this.postedDate = p.getPostedDate().format(fmt);
        } else {
            this.postedDate = "";
        }

        // 상태 → 한글 문자열
        if (p.getStatus() == ProductStatus.FOR_SALE) {
            this.status = "판매 중";
        } else if (p.getStatus() == ProductStatus.RESERVED) {
            this.status = "예약 중";
        } else if (p.getStatus() == ProductStatus.SOLD) {
            this.status = "판매 완료";
        } else {
            this.status = "";
        }

        // 리본 개수 (리스트 size)
        this.ribbons = (p.getRibbons() != null) ? p.getRibbons().size() : 0;

        // 이미지 리스트
        this.images = new ArrayList<>();
        if (p.getProductImage1() != null) this.images.add(p.getProductImage1());
        if (p.getProductImage2() != null) this.images.add(p.getProductImage2());
        if (p.getProductImage3() != null) this.images.add(p.getProductImage3());

        // 위치: 일단 A지역만 "시 구 동" 하나로 묶어서 한 줄로 넣기
        this.locations = new ArrayList<>();
        if (p.getProductLocation1() != null &&
                p.getProductLocation2() != null &&
                p.getProductLocation3() != null) {

            this.locations.add(
                    p.getProductLocation1() + " " +
                            p.getProductLocation2() + " " +
                            p.getProductLocation3()
            );
        }
    }
}

package com.example.kmjoonggo.repository;

import com.example.kmjoonggo.domain.Product;
import com.example.kmjoonggo.domain.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 마이페이지
    List<Product> findBySeller_UserIdOrderByPostedDateDesc(String userId);
    List<Product> findByBuyer_UserIdOrderByPostedDateDesc(String userId);

    // 검색 (부분 검색) — productName 기준
    List<Product> findByProductNameContaining(String productName);

    // 관리자 페이지용 필터
    List<Product> findByProductId(Long productId);
    List<Product> findBySeller_UserId(String userId);
    List<Product> findByCategory(String category);
    List<Product> findByStatus(ProductStatus status);

    // 인기 상품 Top5
    List<Product> findTop10ByStatusOrderByViewsDesc(ProductStatus status);

    List<Product> findByProductNameContainingAndPostedDateAfter(String productName, LocalDateTime after);
}

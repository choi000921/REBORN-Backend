package com.example.kmjoonggo.controller;

import com.example.kmjoonggo.domain.Product;
import com.example.kmjoonggo.dto.*;
import com.example.kmjoonggo.service.ImageAnalyzeService;
import com.example.kmjoonggo.service.ImageCheckService;
import com.example.kmjoonggo.service.ProductService;
import com.example.kmjoonggo.service.RecentViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/products") // /api/products로 시작
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ImageAnalyzeService imageAnalyzeService;
    private final ImageCheckService imageCheckService;
    private final RecentViewService recentViewService;

    // ================== 상품 검색 ==================
    @GetMapping("/search")
    public ResponseEntity<List<ProductSearchResponse>> searchProducts(
            @RequestParam String keyword    // 프론트에서 ?keyword= 로 보낸다고 가정
    ) {
        List<ProductSearchResponse> result = productService.searchProducts(keyword);
        return ResponseEntity.ok(result);
    }

    // ================== 메인 페이지 전체 상품 ==================>
    @GetMapping
    public ResponseEntity<List<ProductMainResponse>> getAllProducts() {
        List<ProductMainResponse> products = productService.getAllProductsForMain();
        return ResponseEntity.ok(products);
    }

    // ================== 인기 상품 Top5 ==================>
    @GetMapping("/top-views")
    public ResponseEntity<List<MainPageProductDto>> getTopViews() {
        List<MainPageProductDto> topProducts = productService.getTopViewedProducts();
        return ResponseEntity.ok(topProducts);
    }

    // ================== 상품 등록 ==================
    /**
     * [기존] 상품 등록 API
     */
    @PostMapping("/upload") // /api/products/upload 주소
    public ResponseEntity<String> uploadProduct(
            @RequestPart("productName") String productName,
            @RequestPart("productDescription") String productDescription,
            @RequestPart("price") String price, // FormData는 숫자를 문자로 보냄
            @RequestPart("category") String category,
            @RequestPart("locations") String locationsJson, // JSON 문자열
            @RequestPart(value = "productImage1", required = false) MultipartFile image1,
            @RequestPart(value = "productImage2", required = false) MultipartFile image2,
            @RequestPart(value = "productImage3", required = false) MultipartFile image3
    ) {
        try {
            List<MultipartFile> images = Stream.of(image1, image2, image3)
                    .filter(file -> file != null && !file.isEmpty()) // null 안전 제거
                    .toList();

            productService.uploadProduct(
                    productName,
                    productDescription,
                    Integer.parseInt(price), // 문자열 -> 숫자로 변환
                    category,
                    locationsJson,
                    images
            );

            return ResponseEntity.ok("상품 등록이 완료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("상품 등록 중 오류 발생: " + e.getMessage());
        }
    }

    // ================== AI 이미지 분석 ==================
    /**
     * [신규] AI 이미지 분석 API
     *
     * @param imageFile 프론트에서 보낸 이미지
     * @return AiAnalyzeResponse (JSON)
     */
    @PostMapping("/analyze-image")
    public ResponseEntity<?> analyzeImage(
            @RequestParam("imageFile") MultipartFile imageFile
    ) {
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            AiAnalyzeResponse response = imageAnalyzeService.analyzeImageWithAi(imageFile);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("이미지 파일 처리 중 오류가 발생했습니다.");
        }
    }

    // ================== 마이페이지: 내 판매/구매 목록 ==================
    @GetMapping("/my-sales")
    public ResponseEntity<List<MySalesListResponse>> getMySales() {
        List<MySalesListResponse> mySalesList = productService.getMySalesList();
        return ResponseEntity.ok(mySalesList);
    }

    @GetMapping("/my-purchases")
    public ResponseEntity<List<MyPurchasesResponse>> getMyPurchases() {
        List<MyPurchasesResponse> myPurchasesList = productService.getMyPurchasesList();
        return ResponseEntity.ok(myPurchasesList);
    }

    // ================== 상품 상세 ==================
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(
            @PathVariable Long productId
    ) {
        ProductDetailResponse detail = productService.getProductDetail(productId);
        return ResponseEntity.ok(detail);
    }

    // ⭐ ================== 리본(찜) 토글 ==================
    @PostMapping("/{productId}/ribbon")
    public ResponseEntity<Map<String, Object>> toggleRibbon(
            @PathVariable Long productId
    ) {
        Map<String, Object> result = productService.toggleRibbon(productId);
        return ResponseEntity.ok(result);
    }

    // ================== 내 최근 본 상품 10개 ==================
    @GetMapping("/recent-views")
    public ResponseEntity<List<MyRecentViewResponse>> getMyRecentViews() {
        List<MyRecentViewResponse> list = recentViewService.getRecentViewsForCurrentUser();
        return ResponseEntity.ok(list);
    }
}

package com.example.kmjoonggo.service;

import com.example.kmjoonggo.domain.Product;
import com.example.kmjoonggo.domain.ProductStatus;
import com.example.kmjoonggo.domain.Ribbon;
import com.example.kmjoonggo.domain.User;
import com.example.kmjoonggo.dto.MainPageProductDto;
import com.example.kmjoonggo.dto.MyPurchasesResponse;
import com.example.kmjoonggo.dto.MySalesListResponse;
import com.example.kmjoonggo.dto.ProductDetailResponse;
import com.example.kmjoonggo.dto.ProductMainResponse;
import com.example.kmjoonggo.dto.ProductSearchResponse;
import com.example.kmjoonggo.repository.ProductRepository;
import com.example.kmjoonggo.repository.RibbonRepository;
import com.example.kmjoonggo.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;
    private final RecentViewService recentViewService;
    // ⭐ 추가: 리본(찜)용 Repository
    private final RibbonRepository ribbonRepository;

    // ================== 공통 조회 ==================

    /** 엔티티 그대로 리턴 (관리자/테스트용) */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /** 메인 페이지용 전체 상품 목록 (간단 DTO) */
    public List<ProductMainResponse> getAllProductsForMain() {
        return productRepository.findAll().stream()
                .map(this::toMainResponse)
                .toList();
    }

    /** 메인 페이지 인기 상품: 판매중 + 조회수 Top 5 */
    @Transactional(readOnly = true)
    public List<MainPageProductDto> getTopViewedProducts() {
        List<Product> topProducts =
                productRepository.findTop10ByStatusOrderByViewsDesc(ProductStatus.FOR_SALE);

        return topProducts.stream()
                .map(MainPageProductDto::new)
                .collect(Collectors.toList());
    }

    /** 상품명 부분검색 (검색 페이지/관리자 공용) */
    @Transactional(readOnly = true)
    public List<ProductSearchResponse> searchProducts(String productName) {
        return productRepository.findByProductNameContaining(productName).stream()
                .map(p -> new ProductSearchResponse(
                        p.getProductId(),
                        p.getProductName(),
                        p.getPrice(),
                        p.getProductImage1(),
                        p.getViews(),
                        p.getRibbons() != null ? p.getRibbons().size() : 0,
                        p.getCategory(),
                        p.getStatus(),
                        p.getProductLocation1(),
                        p.getProductLocation2(),
                        p.getProductLocation3(),
                        p.getPostedDate()
                ))
                .toList();
    }

    /** Product -> 메인페이지용 DTO 변환 */
    private ProductMainResponse toMainResponse(Product p) {
        return new ProductMainResponse(
                p.getProductId(),
                p.getProductName(),
                p.getPrice(),
                p.getProductImage1(),   // 대표 이미지
                p.getViews(),
                p.getPostedDate()
        );
    }

    // ================== 상품 등록 ==================
    @Transactional
    public Long uploadProduct(
            String productName,
            String productDescription,
            int price,
            String category,
            String locationsJson,     // "[{ l1: '...', l2: '...', l3: '...' }, ...]"
            List<MultipartFile> images
    ) throws Exception {

        // 1. 현재 로그인한 유저
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserId(loginId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 2. Product 생성
        Product product = Product.builder()
                .seller(user)
                .productName(productName)
                .productDescription(productDescription)
                .price(price)
                .category(category.equals("(없음)") ? "OTHERS" : category)
                .postedDate(LocalDateTime.now())
                .build();

        // 3. 이미지 세팅 (현재 버전: 파일명만 저장)
        if (images != null) {
            if (images.size() > 0) {
                product.setProductImage1(images.get(0).getOriginalFilename());
            }
            if (images.size() > 1) {
                product.setProductImage2(images.get(1).getOriginalFilename());
            }
            if (images.size() > 2) {
                product.setProductImage3(images.get(2).getOriginalFilename());
            }
        }

        // 4. 지역 정보 파싱 및 세팅
        List<Map<String, String>> locations =
                objectMapper.readValue(locationsJson, new TypeReference<>() {});

        // 지역 A (필수)
        if (!locations.isEmpty()) {
            Map<String, String> locA = locations.get(0);
            product.setProductLocation1(locA.get("l1"));
            product.setProductLocation2(locA.get("l2"));
            product.setProductLocation3(locA.get("l3"));
        }
        // 지역 B (선택)
        if (locations.size() > 1) {
            Map<String, String> locB = locations.get(1);
            product.setProductLocation4(locB.get("l1"));
            product.setProductLocation5(locB.get("l2"));
            product.setProductLocation6(locB.get("l3"));
        }
        // 지역 C (선택)
        if (locations.size() > 2) {
            Map<String, String> locC = locations.get(2);
            product.setProductLocation7(locC.get("l1"));
            product.setProductLocation8(locC.get("l2"));
            product.setProductLocation9(locC.get("l3"));
        }

        // 5. 저장
        Product savedProduct = productRepository.save(product);
        return savedProduct.getProductId();
    }

    // ================== 내 판매 목록 ==================
    @Transactional(readOnly = true)
    public List<MySalesListResponse> getMySalesList() {
        String currentUserId =
                SecurityContextHolder.getContext().getAuthentication().getName();

        List<Product> myProducts =
                productRepository.findBySeller_UserIdOrderByPostedDateDesc(currentUserId);

        return myProducts.stream()
                .map(MySalesListResponse::new)
                .collect(Collectors.toList());
    }

    // ================== 내 구매 목록 ==================
    @Transactional(readOnly = true)
    public List<MyPurchasesResponse> getMyPurchasesList() {
        String currentUserId =
                SecurityContextHolder.getContext().getAuthentication().getName();

        List<Product> myPurchases =
                productRepository.findByBuyer_UserIdOrderByPostedDateDesc(currentUserId);

        return myPurchases.stream()
                .map(MyPurchasesResponse::new)
                .collect(Collectors.toList());
    }

    // ================== 상세 보기 (최근 본 상품 기록 + 찜 여부 포함) ==================
    @Transactional
    public ProductDetailResponse getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품 없음"));

        String loginId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        System.out.println("📌 getProductDetail loginId = " + loginId);

        boolean liked = false;

        if (loginId != null && !"anonymousUser".equals(loginId)) {
            System.out.println("📌 최근 본 상품 저장 호출!");
            recentViewService.addRecentView(loginId, productId);

            // ⭐ 현재 로그인 유저가 이 상품을 찜했는지 여부 확인
            User user = userRepository.findByUserId(loginId).orElse(null);
            if (user != null) {
                liked = ribbonRepository.existsByUserAndProduct(user, product);
            }
        } else {
            System.out.println("⚠️ 로그인 안 된 상태라 recentView 저장 안 함");
        }

        // ⭐ liked 값까지 포함해서 반환
        return new ProductDetailResponse(product, liked);
    }

    // ================== 리본(찜) 토글 ==================
    @Transactional
    public Map<String, Object> toggleRibbon(Long productId) {
        String loginId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        if (loginId == null || "anonymousUser".equals(loginId)) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        User user = userRepository.findByUserId(loginId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품 없음"));

        boolean liked;
        Ribbon existing = ribbonRepository.findByUserAndProduct(user, product).orElse(null);

        if (existing != null) {
            // 이미 찜 → 삭제 (찜 해제)
            ribbonRepository.delete(existing);
            liked = false;
        } else {
            // 아직 안 찜 → 새로 추가
            Ribbon ribbon = Ribbon.builder()
                    .user(user)
                    .product(product)
                    .build();
            ribbonRepository.save(ribbon);
            liked = true;
        }

        long count = ribbonRepository.countByProduct(product);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("ribbons", (int) count);
        return result;
    }
}

// /service/AdminService.java
package com.example.kmjoonggo.service;

import com.example.kmjoonggo.domain.Product;
import com.example.kmjoonggo.domain.ProductStatus;
import com.example.kmjoonggo.domain.User;
import com.example.kmjoonggo.dto.AdminProductDto;
import com.example.kmjoonggo.dto.AdminUserDto;
import com.example.kmjoonggo.repository.ProductRepository;
import com.example.kmjoonggo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.kmjoonggo.domain.User; // (추가)
import com.example.kmjoonggo.dto.AdminUserDetailResponse; // (추가)
import org.springframework.security.core.userdetails.UsernameNotFoundException; // (추가)
import com.example.kmjoonggo.domain.Product; // (1. Product 추가)
import com.example.kmjoonggo.dto.AdminProductDetailResponse; // (2. DTO 추가)
import org.springframework.security.core.userdetails.UsernameNotFoundException; // (오류 처리용)

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList; // (List 생성용)

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * 유저 검색 (동적 필터)
     */
    public List<AdminUserDto> searchUsers(String filter, String term) {
        List<User> users = new ArrayList<>();

        // (기획안) filter 값에 따라 다른 Repository 메소드 호출
        switch (filter) {
            case "userId":
                users = userRepository.findByUserIdContaining(term);
                break;
            case "userName":
                users = userRepository.findByUserNameContaining(term);
                break;
            case "userNickname":
                users = userRepository.findByUserNicknameContaining(term);
                break;
            case "userPhone":
                users = userRepository.findByUserPhoneContaining(term);
                break;
            case "userEmail":
                users = userRepository.findByUserEmailContaining(term);
                break;
            case "userWarning":
                try {
                    users = userRepository.findByUserWarning(Integer.parseInt(term));
                } catch (NumberFormatException e) {
                    // 숫자가 아니면 빈 리스트 반환
                }
                break;
            default:
                // 유효하지 않은 필터
        }

        // User 엔티티 -> AdminUserDto로 변환
        return users.stream().map(AdminUserDto::new).collect(Collectors.toList());
    }

    /**
     * 제품 검색 (동적 필터)
     */
    public List<AdminProductDto> searchProducts(String filter, String term) {
        List<Product> products = new ArrayList<>();

        // (기획안) filter 값에 따라 다른 Repository 메소드 호출
        switch (filter) {
            case "productId":
                try {
                    products = productRepository.findByProductId(Long.parseLong(term));
                } catch (NumberFormatException e) {
                }
                break;
            case "productName":
                products = productRepository.findByProductNameContaining(term);
                break;
            case "sellerId": // (기획안의 'userID'는 sellerId를 의미)
                products = productRepository.findBySeller_UserId(term);
                break;
            case "category":
                products = productRepository.findByCategory(term);
                break;
            case "status":
                try {
                    // (e.g., "FOR_SALE")
                    ProductStatus status = ProductStatus.valueOf(term.toUpperCase());
                    products = productRepository.findByStatus(status);
                } catch (IllegalArgumentException e) {
                }
                break;
            default:
                // 유효하지 않은 필터
        }

        // Product 엔티티 -> AdminProductDto로 변환
        return products.stream().map(AdminProductDto::new).collect(Collectors.toList());
    }
    public AdminUserDetailResponse getUserDetail(String userId) {
        // (참고) User 엔티티 조회 시,
        // @OneToMany(mappedBy="reportedUser")인 warnings 리스트도 함께 조회 (fetch)
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다: " + userId));

        // User 엔티티 -> AdminUserDetailResponse DTO로 변환
        return new AdminUserDetailResponse(user);
    }
    public AdminProductDetailResponse getProductDetail(Long productId) {
        // Product 엔티티를 ID로 조회 (없으면 404 예외)
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new UsernameNotFoundException("상품을 찾을 수 없습니다: " + productId));

        // Product 엔티티 -> AdminProductDetailResponse DTO로 변환
        return new AdminProductDetailResponse(product);
    }

}

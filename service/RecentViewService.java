package com.example.kmjoonggo.service;

import com.example.kmjoonggo.domain.Product;
import com.example.kmjoonggo.domain.RecentView;
import com.example.kmjoonggo.domain.User;
import com.example.kmjoonggo.dto.MyRecentViewResponse;
import com.example.kmjoonggo.repository.ProductRepository;
import com.example.kmjoonggo.repository.RecentViewRepository;
import com.example.kmjoonggo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecentViewService {

    private final RecentViewRepository recentViewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * 상품 상세를 볼 때 호출해서
     * - 있으면 searchedAt만 갱신
     * - 없으면 새로 INSERT
     * - 유저별 30개까지만 남기기
     */
    @Transactional
    public void addRecentView(String userId, Long productId) {

        // 1) 유저, 상품 엔티티 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

        // 2) 이미 본 상품인지 확인
        RecentView recentView = recentViewRepository.findByUserAndProduct(user, product)
                .orElse(null);

        if (recentView == null) {
            // 새 기록 생성
            recentView = RecentView.builder()
                    .user(user)
                    .product(product)
                    .searchedAt(LocalDateTime.now())
                    .build();
            recentViewRepository.save(recentView);
        } else {
            // 기존 기록 시간만 갱신
            recentView.setSearchedAt(LocalDateTime.now());
        }

        // 3) 개수 30개 초과 시, 오래된 것부터 삭제
        long count = recentViewRepository.countByUser(user);
        long over = count - 30;

        while (over > 0) {
            recentViewRepository.findFirstByUserOrderBySearchedAtAsc(user)
                    .ifPresent(recentViewRepository::delete);
            over--;
        }
    }
    @Transactional(readOnly = true)
    public List<MyRecentViewResponse> getRecentViewsForCurrentUser() {

        String loginId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        if (loginId == null || "anonymousUser".equals(loginId)) {
            // 비로그인 상태면 빈 리스트
            return Collections.emptyList();
        }

        User user = userRepository.findByUserId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다: " + loginId));

        List<RecentView> list =
                recentViewRepository.findTop10ByUserOrderBySearchedAtDesc(user);

        // RecentView → Product → MyRecentViewResponse
        return list.stream()
                .map(rv -> new MyRecentViewResponse(rv.getProduct()))
                .toList();
    }
}

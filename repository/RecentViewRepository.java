package com.example.kmjoonggo.repository;

import com.example.kmjoonggo.domain.Product;
import com.example.kmjoonggo.domain.RecentView;
import com.example.kmjoonggo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecentViewRepository extends JpaRepository<RecentView, Long> {

    // 유저 + 상품으로 이미 본 기록 있는지 확인
    Optional<RecentView> findByUserAndProduct(User user, Product product);
    // 유저별 최근 30개 조회 (나중에 마이페이지에서 사용할 예정)
    List<RecentView> findTop30ByUserOrderBySearchedAtDesc(User user);

    // 유저별 전체 개수 (30개 넘는지 확인용)
    long countByUser(User user);

    // 유저의 가장 오래된 기록 1개 (삭제용)
    Optional<RecentView> findFirstByUserOrderBySearchedAtAsc(User user);
    // 🔥 추가: 유저별 최근 본 상품 10개 (최신순)
    List<RecentView> findTop10ByUserOrderBySearchedAtDesc(User user);
}

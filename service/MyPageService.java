// /service/MyPageService.java
package com.example.kmjoonggo.service;

import com.example.kmjoonggo.domain.RecentView;
import com.example.kmjoonggo.domain.Ribbon;
import com.example.kmjoonggo.domain.User;
import com.example.kmjoonggo.dto.MyPageResponse;
import com.example.kmjoonggo.dto.MyRecentViewResponse;
import com.example.kmjoonggo.dto.MyWishlistResponse;
import com.example.kmjoonggo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.kmjoonggo.dto.MyPageEditRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * (핵심) 현재 로그인한 유저의 정보(MyPageResponse)를 반환
     */
    public MyPageResponse getMyInfo() {
        // 1. (인증) Spring Security에서 현재 로그인한 유저의 ID를 가져옴
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. (DB) ID를 기준으로 DB에서 유저 정보를 찾음
        User user = userRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다: " + currentUserId));

        // 3. (변환) User 엔티티 -> MyPageResponse DTO로 변환하여 반환
        return new MyPageResponse(user);
    }
    public List<MyWishlistResponse> getMyWishlist() {
        // 1. 현재 로그인한 유저 ID 찾기
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. (중요) DB에서 User 엔티티를 찾음 (연관된 ribbons 포함)
        User user = userRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다: " + currentUserId));

        // 3. User 엔티티에서 'List<Ribbon>'을 가져옴
        List<Ribbon> myRibbons = user.getRibbons();

        // 4. List<Ribbon> -> List<Product> -> List<MyWishlistResponse>로 변환
        return myRibbons.stream()
                .map(ribbon -> ribbon.getProduct()) // 각 '찜'에서 '상품'을 꺼냄
                .map(MyWishlistResponse::new)     // '상품'을 'DTO'로 변환
                .collect(Collectors.toList());
    }
    public List<MyRecentViewResponse> getMyRecentViews() {
        // 1. 현재 로그인한 유저 ID 찾기
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. DB에서 User 엔티티를 찾음 (연관된 recentViews 포함)
        User user = userRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다: " + currentUserId));

        // 3. User 엔티티에서 'List<RecentView>'를 가져옴
        List<RecentView> myRecentViews = user.getRecentViews();

        // 4. (기획안) 30개 제한 및 최신순 정렬 (Java 스트림 사용)
        return myRecentViews.stream()
                // 'searchedAt' (조회 시간)을 기준으로 내림차순 정렬
                .sorted(Comparator.comparing(RecentView::getSearchedAt).reversed())
                .limit(30) // (규칙) 최대 30개
                .map(recentView -> recentView.getProduct()) // '최근 본 기록'에서 '상품'을 꺼냄
                .map(MyRecentViewResponse::new)     // '상품'을 'DTO'로 변환
                .collect(Collectors.toList());
    }
    @Transactional // (중요) readOnly=false
    public void updateMyInfo(MyPageEditRequest request) {
        // 1. 현재 유저 찾기
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 2. (참고) 닉네임, 이메일, 전화번호가 '나'를 제외한
        //    다른 사람과 중복되는지 검사하는 로직이 필요할 수 있습니다.
        //    (지금은 Entity @UniqueConstraint가 예외를 발생시킬 것임)

        // 3. 정보 업데이트
        user.setUserNickname(request.getUserNickname());
        user.setUserPhone(request.getUserPhone());
        user.setUserEmail(request.getUserEmail());
        user.setUserLocation1(request.getLocation1());
        user.setUserLocation2(request.getLocation2());
        user.setUserLocation3(request.getLocation3());

        // 4. (중요) 새 비밀번호가 입력된 경우에만 업데이트
        if (StringUtils.hasText(request.getNewUserPassword())) {
            // (참고) SecurityConfig이 NoOpPasswordEncoder (암호화 안 함) 기준
            // user.setUserPassword(passwordEncoder.encode(request.getNewUserPassword()));
            user.setUserPassword(request.getNewUserPassword()); // 평문 저장 (임시)
        }

        // (@Transactional이므로 메소드가 끝나면 자동 저장/업데이트됨)
        // userRepository.save(user);
    }
}

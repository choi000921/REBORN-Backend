// /controller/MyPageController.java
package com.example.kmjoonggo.controller;

import com.example.kmjoonggo.dto.MyPageEditRequest;
import com.example.kmjoonggo.dto.MyPageResponse;
import com.example.kmjoonggo.dto.MyRecentViewResponse;
import com.example.kmjoonggo.dto.MyWishlistResponse;
import com.example.kmjoonggo.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users") // (참고) /api/auth가 아닌 /api/users
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * (핵심) "내 정보 조회" API
     * (참고) SecurityConfig에서 /api/** 는 인증이 필요하도록 설정했음
     */
    @GetMapping("/me")
    public ResponseEntity<MyPageResponse> getMyInfo() {
        MyPageResponse responseDto = myPageService.getMyInfo();
        return ResponseEntity.ok(responseDto);
    }
    @GetMapping("/me/wishlist")
    public ResponseEntity<List<MyWishlistResponse>> getMyWishlist() {
        List<MyWishlistResponse> myWishlist = myPageService.getMyWishlist();
        return ResponseEntity.ok(myWishlist);
    }
    @GetMapping("/me/recent-views")
    public ResponseEntity<List<MyRecentViewResponse>> getMyRecentViews() {
        List<MyRecentViewResponse> myRecentViews = myPageService.getMyRecentViews();
        return ResponseEntity.ok(myRecentViews);
    }
    @PutMapping("/me")
    public ResponseEntity<String> updateMyInfo(@RequestBody MyPageEditRequest request) {
        try {
            myPageService.updateMyInfo(request);
            return ResponseEntity.ok("정보 수정이 완료되었습니다.");
        } catch (Exception e) {
            // (참고) @UniqueConstraint 위반 시 DataIntegrityViolationException 발생
            e.printStackTrace();
            return ResponseEntity.status(500).body("정보 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}

// /controller/AdminController.java
package com.example.kmjoonggo.controller;

import com.example.kmjoonggo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.kmjoonggo.dto.AdminUserDetailResponse; // (1. DTO 추가)
import org.springframework.web.bind.annotation.GetMapping; // (GetMapping 확인)
import org.springframework.web.bind.annotation.PathVariable;
import com.example.kmjoonggo.dto.AdminProductDetailResponse; // (1. DTO 추가)
import com.example.kmjoonggo.dto.AdminUserDetailResponse;
import org.springframework.web.bind.annotation.GetMapping; // (GetMapping 확인)
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 관리자 페이지 - 유저/제품 통합 검색 API
     * [GET] /api/admin/search?type=user&filter=userId&term=admin
     * [GET] /api/admin/search?type=product&filter=productName&term=닌텐도
     */
    @GetMapping("/search")
    public ResponseEntity<List<?>> adminSearch(
            @RequestParam("type") String type,
            @RequestParam("filter") String filter,
            @RequestParam("term") String term
    ) {
        if ("user".equals(type)) {
            return ResponseEntity.ok(adminService.searchUsers(filter, term));
        } else if ("product".equals(type)) {
            return ResponseEntity.ok(adminService.searchProducts(filter, term));
        }

        return ResponseEntity.badRequest().build(); // type이 잘못됨
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<AdminUserDetailResponse> getAdminUserDetail(
            @PathVariable String userId
    ) {
        AdminUserDetailResponse userDetail = adminService.getUserDetail(userId);
        return ResponseEntity.ok(userDetail);
    }
    @GetMapping("/product/{productId}")
    public ResponseEntity<AdminProductDetailResponse> getAdminProductDetail(
            @PathVariable Long productId
    ) {
        AdminProductDetailResponse productDetail = adminService.getProductDetail(productId);
        return ResponseEntity.ok(productDetail);
    }
}


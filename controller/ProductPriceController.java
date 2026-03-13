package com.example.kmjoonggo.controller;

import com.example.kmjoonggo.dto.PriceStatsDTO;
import com.example.kmjoonggo.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductPriceController {

    private final PriceService priceService;

    /**
     * 검색어(keyword) 기반으로 시세 조회
     * 예시 요청: GET /api/products/price?keyword=노트북
     */
    @GetMapping("/price")
    public PriceStatsDTO getPriceStats(@RequestParam String keyword) {
        return priceService.getPriceStats(keyword);
    }
}

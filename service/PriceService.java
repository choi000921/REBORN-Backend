package com.example.kmjoonggo.service;

import com.example.kmjoonggo.domain.Product;
import com.example.kmjoonggo.domain.ProductStatus;
import com.example.kmjoonggo.domain.User;
import com.example.kmjoonggo.dto.PriceStatsDTO;
import com.example.kmjoonggo.repository.ProductRepository;
import com.example.kmjoonggo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * 검색어(keyword) 기반으로 최근 1달 시세 조회
     */
    public PriceStatsDTO getPriceStats(String keyword) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        // 최근 1달 거래 상품 조회 (상품 이름에 keyword 포함)
        List<Product> recentProducts =
                productRepository.findByProductNameContainingAndPostedDateAfter(keyword, oneMonthAgo);

        if (recentProducts.isEmpty()) {
            return new PriceStatsDTO(0, 0, 0, 0, 0, "0/0 (0%)");
        }

        // 🔹 최근 1달 단순 평균 거래가
        int recentAveragePrice = (int) recentProducts.stream()
                .mapToInt(Product::getPrice)
                .average()
                .orElse(0);

        // 🔹 가중 평균 계산
        double weightedSum = 0;
        double totalWeight = 0;

        for (Product p : recentProducts) {
            // 1) seller userScore 기반 가중치
            User seller = p.getSeller();
            int userScore = (int) seller.getUserScore(); // userScore가 User 엔티티에 있다고 가정
            double w_user = 1 + ((userScore - 5) / 10.0);

            // 2) 상태 기반 가중치
            double w_status = switch (p.getStatus()) {
                case FOR_SALE -> 1.0;
                case RESERVED -> 1.2;
                case SOLD -> 1.4;
                default -> 1.0;
            };

            // 3) 이미지 수 기반 가중치
            int imageCount = 0;
            if (p.getProductImage1() != null && !p.getProductImage1().isEmpty()) imageCount++;
            if (p.getProductImage2() != null && !p.getProductImage2().isEmpty()) imageCount++;
            if (p.getProductImage3() != null && !p.getProductImage3().isEmpty()) imageCount++;

            double w_image = switch (imageCount) {
                case 0 -> 0.8;
                case 1 -> 1.0;
                default -> 1.2; // 2개 이상
            };

            double weight = w_user * w_status * w_image;
            weightedSum += p.getPrice() * weight;
            totalWeight += weight;
        }

        int weightedPrice = (int) (weightedSum / totalWeight / 1000) * 1000; // 1000원 단위 반올림

        // 🔹 통계 정보 계산
        int highestPrice = recentProducts.stream()
                .filter(p->p.getStatus() == ProductStatus.SOLD)
                .mapToInt(Product::getPrice)
                .max()
                .orElse(0);

        int totalTransactions = recentProducts.size();

        long soldCount = recentProducts.stream()
                .filter(p -> p.getStatus() == ProductStatus.SOLD)
                .count();

        double ratio = totalTransactions > 0
                ? (double) soldCount / totalTransactions
                : 0;

        String soldRatioStr = String.format(
                "%d/%d (%.0f%%)",
                soldCount,
                totalTransactions,
                ratio * 100
        );

        int priceFluctuation = recentProducts.stream()
                .mapToInt(Product::getPrice)
                .max().orElse(0)
                - recentProducts.stream()
                .mapToInt(Product::getPrice)
                .min().orElse(0);

        // 🔹 DTO 생성 후 반환
        return new PriceStatsDTO(
                recentAveragePrice,
                weightedPrice,
                highestPrice,
                totalTransactions,
                priceFluctuation,
                soldRatioStr
        );
    }
}

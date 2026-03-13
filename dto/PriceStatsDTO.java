package com.example.kmjoonggo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriceStatsDTO {
    private int recentAveragePrice;   // 최근 한달 평균 거래가
    private int weightedPrice;        // 가중치 적용 시세
    private int highestPrice;         // 역대 최고 거래가
    private int totalTransactions;    // 총 거래 건수
    private int priceFluctuation;     // 시세 변동폭
    private String soldRatio;
}

// /service/AiCompareService.java
package com.example.kmjoonggo.service;

import com.example.kmjoonggo.domain.Product;
import com.example.kmjoonggo.domain.User;
import com.example.kmjoonggo.dto.AiCompareRequest;
import com.example.kmjoonggo.dto.AiCompareResponse;
import com.example.kmjoonggo.repository.ProductRepository;
import com.example.kmjoonggo.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiCompareService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openai.api-key}")
    private String apiKey;
    @Value("${openai.api-url}")
    private String apiUrl;
    @Value("${openai.model}")
    private String model;

    public AiCompareResponse getAiComparison(AiCompareRequest request) throws Exception {

        // 1. 유저 닉네임 가져오기 (기존)
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserId(loginId)
                .orElseThrow(() -> new RuntimeException("AI 비교: 유저를 찾을 수 없습니다."));
        String userNickname = user.getUserNickname();

        // 2. DB에서 제품 2개 정보 가져오기 (기존)
        Product product1 = productRepository.findById(request.getProductId1())
                .orElseThrow(() -> new RuntimeException("제품 1을 찾을 수 없습니다."));
        Product product2 = productRepository.findById(request.getProductId2())
                .orElseThrow(() -> new RuntimeException("제품 2을 찾을 수 없습니다."));

        // --- (3. (핵심 수정) 1번(데이터 강화) + 3번(점수화 프롬프트)) ---

        // 3-1. 시스템 프롬프트 (규칙 정의)
        String systemPrompt = String.format(
                "You are a pragmatic and friendly shopping advisor for 'RE:BORN'. " +
                        "You are speaking directly to a user named '%s'. " + // 닉네임
                        "Your task is to compare two secondhand items (A and B) and generate a 0-100 score for each, then recommend one. " +
                        "The final score (max 100) is the sum of Score A (max 50) and Score B (max 50). " +

                        // 점수 A 규칙 (메모)
                        "Score A (User Needs, max 50): Analyze how well each item meets the user's 'Memo'. " +

                        // 점수 B 규칙 (데이터 + 가중치)
                        "Score B (Objective Data, max 50): Analyze the item's objective data. " +
                        "Weighting: **Price (Rank 1)**, Views/Ribbons (Rank 2), Posted Date (Rank 3). " +
                        "A *lower price*, *higher views*, *higher ribbons (wishes)*, and *newer posted date* are better. " +
                        "The seller's score also matters for reliability. " +

                        "Respond *only* in the following JSON format: " +
                        "{ \"scoreA\": (int 0-100), \"scoreB\": (int 0-100), " +
                        "\"recommendation\": \"(productA or productB)\", " +
                        "\"reason\": \"[Your final recommendation summary for %s, starting with their name, in Korean.]\", " +
                        "\"prosA\": \"[Pro of item A, in Korean]\", \"consA\": \"[Con of item A, in Korean]\", " +
                        "\"prosB\": \"[Pro of item B, in Korean]\", \"consB\": \"[Con of item B, in Korean]\" }",
                userNickname, userNickname + "님"
        );

        // 3-2. 유저 프롬프트 (데이터 주입)
        String userPrompt = String.format(
                "Here is the data for the two items. Calculate the scores (A+B) and provide the comparison JSON.\n\n" +
                        "--- Item A ---\n" +
                        "Name: %s\n" +
                        "Price: %d원\n" +
                        "Seller Score: %.1f\n" +
                        "Views: %d\n" +
                        "Ribbons (Wishes): %d\n" +
                        "Posted Date: %s\n" +
                        "My Memo for Item A: %s\n\n" +
                        "--- Item B ---\n" +
                        "Name: %s\n" +
                        "Price: %d원\n" +
                        "Seller Score: %.1f\n" +
                        "Views: %d\n" +
                        "Ribbons (Wishes): %d\n" +
                        "Posted Date: %s\n" +
                        "My Memo for Item B: %s",
                // Item A 데이터 (1번 요구사항: DB 데이터 추가)
                product1.getProductName(), product1.getPrice(), product1.getSeller().getUserScore(),
                product1.getViews(), product1.getRibbons().size(),
                product1.getPostedDate().format(DateTimeFormatter.ISO_DATE),
                request.getMemo1(),
                // Item B 데이터
                product2.getProductName(), product2.getPrice(), product2.getSeller().getUserScore(),
                product2.getViews(), product2.getRibbons().size(),
                product2.getPostedDate().format(DateTimeFormatter.ISO_DATE),
                request.getMemo2()
        );
        // --- (프롬프트 수정 완료) ---

        // 4. OpenAI API Payload 생성 (기존과 동일)
        Map<String, Object> systemMessage = Map.of("role", "system", "content", systemPrompt);
        Map<String, Object> userMessage = Map.of("role", "user", "content", userPrompt);

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", model);
        payload.put("messages", List.of(systemMessage, userMessage));
        payload.put("max_tokens", 500);
        payload.put("response_format", Map.of("type", "json_object"));

        // 5. HTTP 헤더 설정 (기존과 동일)
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        // 6. RestTemplate으로 OpenAI API 호출 (기존과 동일)
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

        // 7. (수정) JSON 응답을 DTO로 파싱
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        String jsonContent = rootNode.path("choices").get(0).path("message").path("content").asText();

        // (수정) AI가 보낸 JSON 문자열을 AiCompareResponse DTO로 변환
        AiCompareResponse resultDto = objectMapper.readValue(jsonContent, AiCompareResponse.class);

        return resultDto;
    }
}
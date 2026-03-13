// /service/ImageAnalyzeService.java
package com.example.kmjoonggo.service;

import com.example.kmjoonggo.dto.AiAnalyzeResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ImageAnalyzeService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.api-url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String model; // (application.properties에서 "gpt-4o"를 읽어옴)

    public ImageAnalyzeService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * RestTemplate을 사용해 OpenAI API를 직접 호출합니다.
     */
    public AiAnalyzeResponse analyzeImageWithAi(MultipartFile imageFile) throws IOException {

        // 1. 이미지를 Base64 문자열로 인코딩
        String base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());
        String imageUrl = "data:" + imageFile.getContentType() + ";base64," + base64Image;

        // 2. (수정) AI 프롬프트 (영어로 번역)
        String systemPrompt = "You are an expert marketplace assistant for a site called RE:BORN. " +
                "Look at this image and select the *single* most appropriate category from this list of 10: " +
                "[디지털기기, 가구/인테리어, 유아동, 생활가전, 스포츠/레저, 여성의류, 남성의류, 게임/취미, 도서/티켓, 기타]. " +
                "Respond *only* in the following JSON format. " +
                "**IMPORTANT: The response for productName and productDescription must be in Korean.**"; // (수정) 한글 응답 요구

        String userPrompt = "Generate a JSON object with keys: productName (string, 10 words or less, in Korean), " +
                "category (string, from the provided list), " +
                "and productDescription (string, a 3-line summary of the item's condition and features, in Korean).";


        // 3. OpenAI API Payload 생성
        Map<String, Object> textContent = Map.of("type", "text", "text", userPrompt);
        Map<String, Object> imageUrlContent = Map.of("type", "image_url", "image_url", Map.of("url", imageUrl));

        Map<String, Object> systemMessage = Map.of("role", "system", "content", systemPrompt);
        Map<String, Object> userMessage = Map.of("role", "user", "content", List.of(textContent, imageUrlContent));

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", model); // (gpt-4o)
        payload.put("messages", List.of(systemMessage, userMessage));
        payload.put("max_tokens", 300);
        payload.put("response_format", Map.of("type", "json_object"));

        // 4. HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            // 6. RestTemplate으로 OpenAI API 호출
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            // 7. JSON 응답 파싱
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            String jsonContent = rootNode.path("choices").get(0).path("message").path("content").asText();
            JsonNode contentNode = objectMapper.readTree(jsonContent);

            String productName = contentNode.path("productName").asText("AI 분석 실패");
            String category = contentNode.path("category").asText("기타");
            String description = contentNode.path("productDescription").asText("AI 분석 실패");

            return new AiAnalyzeResponse(productName, category, description);

        } catch (Exception e) {
            e.printStackTrace();
            return new AiAnalyzeResponse("AI 분석 오류", "기타", "AI 서버와 통신 중 오류가 발생했습니다.");
        }
    }
}
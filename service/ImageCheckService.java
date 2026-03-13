// /service/ImageCheckService.java
package com.example.kmjoonggo.service;

import com.example.kmjoonggo.domain.User;
import com.example.kmjoonggo.domain.Warning;
import com.example.kmjoonggo.dto.ImageCheckResponse;
import com.example.kmjoonggo.repository.UserRepository;
import com.example.kmjoonggo.repository.WarningRepository;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageCheckService {

    private final ImageAnnotatorClient imageAnnotatorClient;
    private final UserRepository userRepository;
    private final WarningRepository warningRepository;

    // (임시) 관리자 ID (userTB에 'admin' ID가 있어야 함)
    private static final String MASTER_ID = "admin";

    @Transactional
    public ImageCheckResponse checkImageRisk(MultipartFile file) throws IOException {

        ByteString imgBytes = ByteString.copyFrom(file.getBytes());
        Image image = Image.newBuilder().setContent(imgBytes).build();
        Feature feature = Feature.newBuilder().setType(Feature.Type.WEB_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feature)
                .setImage(image)
                .build();
        BatchAnnotateImagesResponse response = imageAnnotatorClient.batchAnnotateImages(
                Collections.singletonList(request)
        );
        WebDetection webDetection = response.getResponses(0).getWebDetection();

        // --- (이 부분이 수정되었습니다) ---

        // 1. High Risk (고위험)
        // (가장 강력한 신호: 1:1로 정확히 일치하는 페이지)
        if (webDetection.getPagesWithMatchingImagesCount() > 0) {
            String matchingUrl = webDetection.getPagesWithMatchingImages(0).getUrl();

            // 경고 로직 실행
            handleWarning("이미지 도용 의심: " + matchingUrl);

            return new ImageCheckResponse("High", "다른 웹사이트(" + matchingUrl + ")에서 동일한 이미지가 발견되었습니다.");
        }

        // 2. Medium Risk (중위험)
        // (중간 신호: 이미지가 잘리거나 변형되어 사용됨)
        if (webDetection.getPartialMatchingImagesCount() > 0) {
            return new ImageCheckResponse("Medium", "부분적으로 일치하는 이미지가 발견되었습니다. 도용된 사진일 수 있습니다.");
        }

        // 3. (수정) '시각적으로 유사한(VisuallySimilarImages)' 이미지는
        //    이제 "중위험"이 아닌 "낮음(Low)"으로 분류합니다.
        //    이 로직을 제거함으로써 민감도가 대폭 낮아집니다.
        /*
        if (webDetection.getVisuallySimilarImagesCount() > 0) {
            return new ImageCheckResponse("Medium", "유사한 이미지가 발견되었습니다.");
        }
        */

        // 4. Low Risk (낮음)
        return new ImageCheckResponse("Low", "확인된 사진입니다.");

        // --- (여기까지) ---
    }


    // (경고 처리 메소드 - 기존과 동일)
    @Transactional
    private void handleWarning(String comment) {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        User reportedUser = userRepository.findByUserId(loginId)
                .orElseThrow(() -> new RuntimeException("경고 처리 중 (신고 대상) 유저를 찾을 수 없음: " + loginId));

        User reporterUser = userRepository.findByUserId(MASTER_ID)
                .orElseThrow(() -> new RuntimeException("경고 처리 중 (관리자) 유저를 찾을 수 없음: " + MASTER_ID));

        reportedUser.setUserWarning(reportedUser.getUserWarning() + 1);

        Warning warning = Warning.builder()
                .reportedUser(reportedUser)
                .reporterUser(reporterUser)
                .comment(comment)
                .product(null) // (상품 등록 전이므로 null)
                .build();

        warningRepository.save(warning);
    }
}
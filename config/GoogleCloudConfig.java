// /config/GoogleCloudConfig.java

package com.example.kmjoonggo.config;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource; // (import 추가)

import java.io.IOException;
import java.io.InputStream; // (import 추가)

@Configuration
public class GoogleCloudConfig {

    // (수정 1) application.properties에서 키 파일 경로를 @Value로 가져옵니다.
    @Value("${spring.cloud.gcp.credentials.location}")
    private Resource gcpCredentials;

    @Bean
    public ImageAnnotatorClient imageAnnotatorClient() throws IOException {

        // (수정 2) 리소스 폴더에서 JSON 키 파일을 InputStream으로 불러옵니다.
        InputStream credentialsStream = gcpCredentials.getInputStream();
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);

        // (수정 3) 이 특정 인증 정보를 사용하도록 클라이언트 설정을 만듭니다.
        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        // (수정 4) 이 설정을 사용하여 클라이언트를 생성합니다.
        return ImageAnnotatorClient.create(settings);
    }
}
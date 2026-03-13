// /dto/AuthResponse.java
package com.example.kmjoonggo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
 // 모든 필드를 인자로 받는 생성자
public class AuthResponse {
    private String userId;
    private String nickname;
    private String accessToken;
    private int userClass;


    public AuthResponse(String userId, String nickname, String accessToken, int userClass) {
        this.userId = userId;
        this.nickname = nickname;
        this.accessToken = accessToken;
        this.userClass = userClass;
    }
}

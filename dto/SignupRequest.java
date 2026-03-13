// /dto/SignupRequest.java
package com.example.kmjoonggo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

    // 1. 로그인 정보
    private String loginId;     // (추가)
    private String password;    // (기존)
    // '비밀번호 확인'은 프론트엔드에서만 검증

    // 2. 개인 정보
    private String name;        // (추가)
    private String nickname;    // (기존)
    private String phoneNumber; // (추가)
    private String email;       // (기존)

    // (중요) 프론트엔드에서 'YYYY-MM-DD' 형식의 문자열로 받아야 함
    private String birthDate;   // (추가)

    private String location1;
    private String location2;
    private String location3;

    // (참고)
    // location1_id, email_verified 등은
    // 회원가입 '이후'에 '이메일 인증', '마이페이지 수정' 등을 통해
    // 별도로 처리하는 것이 일반적이라 여기서는 제외했습니다.
}
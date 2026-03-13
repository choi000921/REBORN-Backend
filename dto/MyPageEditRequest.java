// /dto/MyPageEditRequest.java
package com.example.kmjoonggo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyPageEditRequest {
    // 수정 가능한 필드들
    private String userNickname;
    private String newUserPassword; // (비밀번호 변경 시에만 사용)
    private String userPhone;
    private String userEmail;
    private String location1;
    private String location2;
    private String location3;
}
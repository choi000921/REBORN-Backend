// /dto/AdminUserDto.java
package com.example.kmjoonggo.dto;

import com.example.kmjoonggo.domain.User;
import lombok.Getter;

@Getter
public class AdminUserDto {
    // AdminUserList.js가 요구하는 필드
    private String userId;
    private String userName;
    private String userNickname;

    // --- (이 생성자가 image_45e08c.png 오류를 해결합니다) ---
    // User 엔티티를 AdminUserDto로 변환하는 생성자
    public AdminUserDto(User user) {
        this.userId = user.getUserId();
        this.userName = user.getUsername();
        this.userNickname = user.getUserNickname();
    }
    // --- (여기까지) ---
}
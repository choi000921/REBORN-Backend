// /dto/AdminUserDetailResponse.java
package com.example.kmjoonggo.dto;

import com.example.kmjoonggo.domain.User;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class AdminUserDetailResponse {

    // (기본 정보 10가지)
    private String userId;
    private String userName;
    private String userNickname;
    private String userPassword; // (보안 주의)
    private String userPhone;
    private String userEmail;
    private LocalDate userBirthday;
    private String userLocation;
    private double userScore;
    private int userWarning;

    // (경고 리스트)
    private List<AdminWarningDto> warnings;

    public AdminUserDetailResponse(User user) {
        this.userId = user.getUserId();
        this.userName = user.getUsername();
        this.userNickname = user.getUserNickname();
        this.userPassword = "**********"; // (규칙) 비밀번호 숨김
        this.userPhone = user.getUserPhone();
        this.userEmail = user.getUserEmail();
        this.userBirthday = user.getUserBirthday();
        this.userLocation = user.getUserLocation1() + " " + user.getUserLocation2() + " " + user.getUserLocation3();
        this.userScore = user.getUserScore(); // (userLike/Dislike)
        this.userWarning = user.getUserWarning();

        // User 엔티티의 'reportsReceived' 리스트를 DTO로 변환
        this.warnings = user.getReportsReceived().stream()
                .map(AdminWarningDto::new)
                .collect(Collectors.toList());
    }
}
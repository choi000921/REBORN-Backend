// /dto/MyPageResponse.java
package com.example.kmjoonggo.dto;

import com.example.kmjoonggo.domain.User;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class MyPageResponse {

    // 와이어프레임의 {userName}, {userNickname} 등
    private String userName;
    private String userNickname;
    private double userScore;
    private String userPhone;
    private String userBirthday; // (참고) LocalDate -> String으로 변환
    private String userEmail;
    private String userLocation; // (참고) 3개 필드를 합친 문자열
    private int salesCount;      // 판매 수
    private int purchasesCount;  // 구매 수

    // (생성자) User 엔티티를 받아서 -> MyPageResponse DTO로 변환
    public MyPageResponse(User user) {
        this.userName = user.getUsername();
        this.userNickname = user.getUserNickname();
        this.userScore = user.getUserScore();
        this.userPhone = user.getUserPhone();
        this.userBirthday = user.getUserBirthday().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.userEmail = user.getUserEmail();

        // (규칙) userLocation 3가지를 "경기도 수원시 영통구"처럼 합칩니다.
        this.userLocation = user.getUserLocation1() + " " +
                user.getUserLocation2() + " " +
                user.getUserLocation3();

        this.salesCount = user.getProducts().size(); // (참고) 'products'는 User 엔티티의 판매 목록 필드

        // (수정) 'purchases' 필드 대신 'buyerChatRooms' 필드의 size()를 사용
        // (참고: 기획에 따라 '구매 확정'된 Product 리스트를 User가 가져야 할 수도 있습니다.)
        this.purchasesCount = user.getBuyerChatRooms().size();
    }
}
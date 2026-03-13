package com.example.kmjoonggo.domain;

import jakarta.persistence.*;
import lombok.*;
// (추가) Spring Security (UserDetails)
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(
        name = "userTB",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"userPhone"}),
                @UniqueConstraint(columnNames = {"userNickname"}),
                @UniqueConstraint(columnNames = {"userEmail"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails { // (1. 수정) UserDetails 구현

    @Id
    @Column(nullable = false, length = 50)
    private String userId; // (Spring Security의 'username'으로 사용됨)

    @Column(nullable = false, length = 50)
    private String userName;

    @Column(nullable = false, length = 50)
    private String userNickname;

    @Column(nullable = false)
    private String userPassword; // (Spring Security의 'password'로 사용됨)

    @Column(nullable = false, length = 20)
    private String userPhone;

    @Column(nullable = false, length = 100)
    private String userEmail;

    @Column(nullable = false)
    private LocalDate userBirthday;

    @Column(nullable = false, length = 50)
    private String userLocation1;

    @Column(nullable = false, length = 50)
    private String userLocation2;

    @Column(nullable = false, length = 50)
    private String userLocation3;

    @Builder.Default // (2. 수정) Builder 경고 해결
    @Column(nullable = false)
    private double userScore = 0.0;

    @Builder.Default // (2. 수정) Builder 경고 해결
    @Column(nullable = false)
    private int userWarning = 0;

    @Builder.Default // (2. 수정) Builder 경고 해결
    @Column(nullable = false)
    private int userClass = 0; // 0: 일반유저, 1: 관리자

    @Builder.Default // (2. 수정) Builder 경고 해결
    @Column(nullable = false)
    private boolean emailVerified = false; // 이메일 인증 여부

    @Builder.Default // (2. 수정) Builder 경고 해결
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    @Builder.Default // (2. 수정) Builder 경고 해결
    @OneToMany(mappedBy = "reporterUser", cascade = CascadeType.ALL)
    private List<Warning> reportsMade = new ArrayList<>();

    @Builder.Default // (2. 수정) Builder 경고 해결
    @OneToMany(mappedBy = "reportedUser", cascade = CascadeType.ALL)
    private List<Warning> reportsReceived = new ArrayList<>();

    @Builder.Default // (2. 수정) Builder 경고 해결
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Ribbon> ribbons = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<RecentView> recentViews = new ArrayList<>();

    @Builder.Default // (2. 수정) Builder 경고 해결
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private List<ChatRoom> sellerChatRooms = new ArrayList<>();


    @Builder.Default // (2. 수정) Builder 경고 해결
    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL)
    private List<ChatRoom> buyerChatRooms = new ArrayList<>();

    // --- (3. 추가) UserDetails 필수 구현 메소드 8개 ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = (this.userClass == 1) ? "ROLE_ADMIN" : "ROLE_USER";
        return Collections.singletonList(() -> role);
    }

    @Override
    public String getPassword() {
        return this.userPassword;
    }

    @Override
    public String getUsername() {
        return this.userId;
    }

    @Override
    public boolean isAccountNonExpired() { return true; } // 계정 만료 안 됨

    @Override
    public boolean isAccountNonLocked() { return true; } // 계정 안 잠김

    @Override
    public boolean isCredentialsNonExpired() { return true; } // 비밀번호 만료 안 됨

    @Override
    public boolean isEnabled() {
        // (참고) 이메일 인증을 활성화하려면 이 값을 true로 하거나,
        // 회원가입 시 emailVerified를 true로 설정해야 합니다.
        // return this.emailVerified;
        return true; // (임시로 true로 설정하여 계정 활성화)
    }
    // --- (여기까지) ---
}
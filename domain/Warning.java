// /domain/Warning.java
package com.example.kmjoonggo.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "warningTB") // DB 테이블명
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- (이하 User.java의 @OneToMany와 1:1 매핑) ---

    // User.java의 'reportsMade' (신고한 내역)와 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_user_id") // DB 컬럼명
    private User reporterUser;

    // User.java의 'reportsReceived' (신고받은 내역)와 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id", nullable = false) // (수정) 500 오류 원인
    private User reportedUser;

    // --- (여기까지) ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true) // null 허용
    private Product product;

    @Column(nullable = false)
    private String comment; // "이미지 도용 의심"

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
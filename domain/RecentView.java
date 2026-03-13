package com.example.kmjoonggo.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recentViewTB")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Column(nullable = false)
    private LocalDateTime searchedAt;

    @PrePersist
    protected void onCreate() {
        searchedAt = LocalDateTime.now();
    }
}

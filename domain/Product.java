package com.example.kmjoonggo.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productTB")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = true)
    private User buyer;


    @Column(nullable = false, length = 255)
    private String productName;

    @Column(columnDefinition = "TEXT")
    private String productDescription;

    private String productImage1;
    private String productImage2;
    private String productImage3;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    @Builder.Default
    private int views = 0;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String category = "OTHERS";

    @Column(nullable = false)
    private LocalDateTime postedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.FOR_SALE;

    @Column(nullable = false, length = 50)
    private String productLocation1;

    @Column(nullable = false, length = 50)
    private String productLocation2;

    @Column(nullable = false, length = 50)
    private String productLocation3;

    @Column(length = 50)
    private String productLocation4;

    @Column(length = 50)
    private String productLocation5;

    @Column(length = 50)
    private String productLocation6;

    @Column(length = 50)
    private String productLocation7;

    @Column(length = 50)
    private String productLocation8;

    @Column(length = 50)
    private String productLocation9;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Ribbon> ribbons = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Warning> warnings = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Builder.Default
    private List<RecentView> recentViews = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ChatRoom> chatRooms = new ArrayList<>();
}

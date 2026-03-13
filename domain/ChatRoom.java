package com.example.kmjoonggo.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chatRoomTB")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    private LocalDateTime updatedAt;

    private LocalDateTime sellerLastSeen;

    private LocalDateTime buyerLastSeen;

    @Builder.Default // <-- (수정)
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Chat> chats = new ArrayList<>();
}
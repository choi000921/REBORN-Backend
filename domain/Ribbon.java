package com.example.kmjoonggo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "ribbonTB",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ribbon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ribbonId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}

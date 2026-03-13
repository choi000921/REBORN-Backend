package com.example.kmjoonggo.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categoryTB")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    @Builder.Default // <-- (수정)
    private CategoryType category = CategoryType.OTHERS;

    @Builder.Default // <-- (수정)
    @Column(nullable = false)
    private int count = 0;

    @Builder.Default // <-- (수정)
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();
}
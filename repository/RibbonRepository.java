package com.example.kmjoonggo.repository;

import com.example.kmjoonggo.domain.Product;
import com.example.kmjoonggo.domain.Ribbon;
import com.example.kmjoonggo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RibbonRepository extends JpaRepository<Ribbon, Long> {

    Optional<Ribbon> findByUserAndProduct(User user, Product product);

    boolean existsByUserAndProduct(User user, Product product);

    long countByProduct(Product product);
}

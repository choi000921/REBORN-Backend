// /repository/WarningRepository.java
package com.example.kmjoonggo.repository;

import com.example.kmjoonggo.domain.Warning;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarningRepository extends JpaRepository<Warning, Long> {
}
// /repository/UserRepository.java
package com.example.kmjoonggo.repository;

import com.example.kmjoonggo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// (수정) JpaRepository<User, Long> -> <User, String> (PK 타입 변경)
public interface UserRepository extends JpaRepository<User, String> {

    // (수정) Spring Security 로그인용
    Optional<User> findByUserId(String userId);

    // (추가) 회원가입 중복 체크용
    boolean existsByUserId(String userId);
    boolean existsByUserNickname(String userNickname);
    boolean existsByUserEmail(String userEmail);
    boolean existsByUserPhone(String userPhone);

    // (관리자 관련 정욱이가 추가)
    List<User> findByUserIdContaining(String userId);
    List<User> findByUserNameContaining(String userName);
    List<User> findByUserNicknameContaining(String userNickname);
    List<User> findByUserPhoneContaining(String userPhone);
    List<User> findByUserEmailContaining(String userEmail);
    List<User> findByUserWarning(int userWarning); // (경고 횟수는 정확히 일치)
}
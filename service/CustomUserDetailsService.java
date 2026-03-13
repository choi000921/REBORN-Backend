// /service/CustomUserDetailsService.java
package com.example.kmjoonggo.service;

import com.example.kmjoonggo.domain.User;
import com.example.kmjoonggo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // (수정) loginId -> userId, findByLoginId -> findByUserId
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 아이디를 찾을 수 없습니다: " + userId));

        return user; // User가 UserDetails를 구현했으므로 바로 반환 가능
    }
}
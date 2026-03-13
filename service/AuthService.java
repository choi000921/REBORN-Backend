// /service/AuthService.java
package com.example.kmjoonggo.service;

import com.example.kmjoonggo.domain.User;
import com.example.kmjoonggo.dto.AuthResponse;
import com.example.kmjoonggo.dto.LoginRequest;
import com.example.kmjoonggo.dto.SignupRequest;
import com.example.kmjoonggo.repository.UserRepository;
import com.example.kmjoonggo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

// (추가) 이메일 인증 코드 저장용
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    // ==========================
    // (추가) 이메일 인증용 메모리 저장소
    // ==========================
    // email -> code
    private final Map<String, String> emailCodeStore = new ConcurrentHashMap<>();

    // 인증 완료된 email 목록
    private final Set<String> verifiedEmailStore = ConcurrentHashMap.newKeySet();

    // ==========================
    // (추가) 이메일 인증 코드 발송 (콘솔에만 찍기)
    // ==========================
    public void sendEmailCode(String email) {
        // 이미 가입된 이메일이면 막고 싶으면 이 로직 유지
        if (userRepository.existsByUserEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 6자리 랜덤 코드 생성 (000000 ~ 999999)
        String code = String.format("%06d",
                ThreadLocalRandom.current().nextInt(0, 1000000));

        emailCodeStore.put(email, code);
        verifiedEmailStore.remove(email); // 다시 인증받도록 초기화

        // 실제 이메일은 안 보내고, 콘솔에만 출력
        System.out.println("[이메일 인증] " + email + " 의 인증 코드 = " + code);
    }

    // ==========================
    // (추가) 이메일 인증 코드 검증
    // ==========================
    public void verifyEmailCode(String email, String code) {
        String saved = emailCodeStore.get(email);

        if (saved == null || !saved.equals(code)) {
            throw new IllegalArgumentException("인증 코드가 올바르지 않습니다.");
        }

        // 코드가 일치하면 이 이메일은 인증 완료로 표시
        verifiedEmailStore.add(email);
        System.out.println("[이메일 인증] " + email + " 인증 완료");
    }

    /**
     * 회원가입
     */
    @Transactional
    public String signup(SignupRequest request) { // (1. 수정) 반환 타입 Long -> String
        // (참고) UserRepository에 existsByUserId, existsByUserNickname 등이 있어야 함
        if (userRepository.existsByUserId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        if (userRepository.existsByUserNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
        if (userRepository.existsByUserEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (userRepository.existsByUserPhone(request.getPhoneNumber())) {
            throw new IllegalArgumentException("이미 존재하는 전화번호입니다.");
        }

        // ==========================
        // (추가) 이메일 인증 여부 서버에서 한 번 더 체크
        // ==========================
        if (!verifiedEmailStore.contains(request.getEmail())) {
            throw new IllegalStateException("이메일 인증을 먼저 완료해주세요.");
        }

        // (수정) SecurityConfig에서 NoOpPasswordEncoder (암호화 안 함) 사용 중
        // String encodedPassword = passwordEncoder.encode(request.getPassword());
        String encodedPassword = request.getPassword(); // (임시) 평문 저장

        // (수정) User 엔티티의 새 필드명으로 Builder 수정
        User newUser = User.builder()
                .userId(request.getLoginId())
                .userPassword(encodedPassword)
                .userName(request.getName())
                .userNickname(request.getNickname())
                .userPhone(request.getPhoneNumber())
                .userEmail(request.getEmail())
                .userBirthday(LocalDate.parse(request.getBirthDate()))
                .userLocation1(request.getLocation1()) // (추가)
                .userLocation2(request.getLocation2()) // (추가)
                .userLocation3(request.getLocation3()) // (추가)
                .emailVerified(true) // 이메일 인증 완료 상태로 저장
                .build();

        userRepository.save(newUser);

        // (추가) 회원가입이 끝났으면 해당 이메일 관련 인증 정보 제거 (선택)
        emailCodeStore.remove(request.getEmail());
        verifiedEmailStore.remove(request.getEmail());

        return newUser.getUserId(); // (2. 수정) getId() -> getUserId()
    }

    /**
     * 로그인
     */
    public AuthResponse login(LoginRequest request) {

        System.out.println("--- [AuthService.login] ---");
        System.out.println("React가 보낸 loginId: " + request.getLoginId());
        System.out.println("React가 보낸 password: " + request.getPassword());
        System.out.println("---------------------------");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLoginId(),
                        request.getPassword()
                )
        );

        String accessToken = jwtTokenProvider.createToken(authentication);
        User user = (User) authentication.getPrincipal();

        // (수정) getId() -> getUserId(), getNickname() -> getUserNickname()
        return new AuthResponse(user.getUserId(), user.getUserNickname(), accessToken, user.getUserClass());
    }
}

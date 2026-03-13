// /controller/AuthController.java
package com.example.kmjoonggo.controller;

import com.example.kmjoonggo.dto.AuthResponse;
import com.example.kmjoonggo.dto.LoginRequest;
import com.example.kmjoonggo.dto.SignupRequest;
import com.example.kmjoonggo.service.AuthService;
import com.example.kmjoonggo.dto.EmailVerifyRequest; // (추가)

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam; // (추가)

@RestController
@RequestMapping("/api/auth") // (중요) /api/auth 로 시작하는 모든 요청
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 1. 회원가입 API
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {

        try {
            authService.signup(signupRequest);
            return new ResponseEntity<>("회원가입 성공", HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) { // (추가) 이메일 미인증도 400으로
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400 에러
        }
    }

    /**
     * 2. 로그인 API
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {

        // (참고) 로그인이 실패하면(비번 틀림 등)
        // AuthService -> AuthenticationManager가 401 에러를 알아서 던져줍니다.
        AuthResponse response = authService.login(loginRequest);

        // 로그인 성공 시 AuthResponse(userId, nickname, accessToken) 반환
        return ResponseEntity.ok(response);
    }

    /**
     * 3. 이메일 인증 코드 발송 API
     *    - 프론트에서: POST /api/auth/email-code?email=xxx@xxx.com
     */
    @PostMapping("/email-code") // (추가)
    public ResponseEntity<String> sendEmailCode(@RequestParam String email) {
        authService.sendEmailCode(email);
        return ResponseEntity.ok("인증 코드를 발송했습니다. (백엔드 콘솔에서 확인)");
    }

    /**
     * 4. 이메일 인증 코드 검증 API
     *    - 프론트에서: POST /api/auth/verify-email
     *      { "email": "...", "code": "123456" }
     */
    @PostMapping("/verify-email") // (추가)
    public ResponseEntity<String> verifyEmail(@RequestBody EmailVerifyRequest request) {
        authService.verifyEmailCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }
}

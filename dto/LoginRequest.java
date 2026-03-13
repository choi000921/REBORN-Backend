// /dto/LoginRequest.java
package com.example.kmjoonggo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String loginId; // (수정) email -> loginId
    private String Password;
}
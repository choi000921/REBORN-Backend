// /dto/EmailVerifyRequest.java
package com.example.kmjoonggo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerifyRequest {

    private String email; // 어떤 이메일인지
    private String code;  // 사용자가 입력한 6자리 코드
}

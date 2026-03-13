// /dto/AiCompareRequest.java
package com.example.kmjoonggo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiCompareRequest {
    private Long productId1;
    private Long productId2;
    private String memo1;
    private String memo2;
}
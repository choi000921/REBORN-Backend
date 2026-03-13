// /dto/AdminWarningDto.java
package com.example.kmjoonggo.dto;

import com.example.kmjoonggo.domain.Warning;
import lombok.Getter;

@Getter
public class AdminWarningDto {

    private Long id; // 경고 ID
    private Long productId;
    private String reporterUserId;
    private String comment;

    public AdminWarningDto(Warning warning) {
        this.id = warning.getId();
        // (참고) 'product'가 null일 수 있으므로 Null 체크
        this.productId = (warning.getProduct() != null) ? warning.getProduct().getProductId() : null;
        this.reporterUserId = warning.getReporterUser().getUserId();
        this.comment = warning.getComment();
    }
}
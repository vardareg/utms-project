package com.iztech.utms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YksValidationResponse {
    private boolean valid;
    private BigDecimal officialScore;
    private Integer examYear;
    private String message;
}

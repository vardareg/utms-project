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
public class TranscriptDto {
    private String courseCode;
    private String courseName;
    private BigDecimal grade;
    private Integer credit;
    private String semester;
    private Integer year;
}

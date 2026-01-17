package com.iztech.utms.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class StudentProfileDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "TCKN is required")
        @Size(min = 11, max = 11, message = "TCKN must be exactly 11 characters")
        @Pattern(regexp = "^[0-9]+$", message = "TCKN must contain only numbers")
        private String tckn;

        @NotBlank(message = "Current University is required")
        private String currentUniversity;

        @NotBlank(message = "Current Program is required")
        private String currentProgram;

        @NotNull(message = "GPA is required")
        @DecimalMin(value = "0.00", message = "GPA cannot be less than 0.00")
        @DecimalMax(value = "4.00", message = "GPA cannot be greater than 4.00")
        private BigDecimal overallGpa;

        private boolean hasDisciplinaryRecord;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String tckn;
        private String currentUniversity;
        private String currentProgram;
        private BigDecimal overallGpa;
        private boolean hasDisciplinaryRecord;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
    }
}

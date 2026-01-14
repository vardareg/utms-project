package com.iztech.utms.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

public class ApplicationDTO {

    @Data
    public static class Request {
        @NotNull(message = "Target Department is required")
        private Integer targetDepartmentId;

        @NotNull(message = "YKS Score is required")
        @DecimalMin(value = "0.0", message = "YKS Score must be positive")
        private BigDecimal yksScore;
    }

    @Data
    public static class Response {
        private Long trackingId;
        private String studentName; // Added for OIDB view
        private String status;
        private String submissionDate;
        private String departmentName;
        private Integer targetDepartmentId; // Added for frontend pre-fill
        private BigDecimal compositeScore;
        private BigDecimal yksScore;
        private BigDecimal gpa;
        private String dataVerificationStatus; // Added field
        private List<DocumentResponse> documents; // WP-4: Critical for validation
        private String returnReason; // Added for Resubmission UI
    }

    @Data
    public static class DocumentResponse {
        private Long id;
        private String type;
        private String fileName;
    }
}
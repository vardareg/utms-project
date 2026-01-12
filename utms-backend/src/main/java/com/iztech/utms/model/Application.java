package com.iztech.utms.model;

import com.iztech.utms.model.UniversityStructure.Department;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Maps to 'applications' table.
 * Core Domain Object for UC-STU-01.
 */
@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_department_id", nullable = false)
    private Department targetDepartment;

    @Column(name = "yks_score", nullable = false, precision = 6, scale = 3)
    private BigDecimal yksScore;

    @Column(name = "converted_gpa", nullable = false, precision = 3, scale = 2)
    private BigDecimal convertedGpa;

    // PR-07: Composite Transfer Score
    @Column(name = "composite_score", precision = 6, scale = 3)
    private BigDecimal compositeScore;

    @CreationTimestamp
    @Column(name = "submission_date", updatable = false)
    private LocalDateTime submissionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status;

    @Column(name = "return_reason", columnDefinition = "TEXT")
    private String returnReason;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents;

    public enum ApplicationStatus {
        NEW, FORWARDED, UNDER_REVIEW, FINALIZED, APPROVED, REJECTED, RETURNED, RESUBMITTED
    }
}
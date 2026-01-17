package com.iztech.utms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

/**
 * Maps to 'student_profiles' table.
 * One-to-One relationship with User.
 */
@Entity
@Table(name = "student_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank
    @Column(nullable = false, unique = true, length = 11)
    private String tckn;

    @Column(name = "current_university", length = 100)
    private String currentUniversity;

    @Column(name = "current_program", length = 100)
    private String currentProgram;

    @Builder.Default
    @Column(name = "has_disciplinary_record", nullable = false)
    private boolean hasDisciplinaryRecord = false;

    // PR-01: Minimum GPA Rule foundation
    @DecimalMin("0.00")
    @DecimalMax("4.00")
    @Column(name = "overall_gpa", precision = 3, scale = 2)
    private BigDecimal overallGpa;
}
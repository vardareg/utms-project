package com.iztech.utms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Maps to 'users' table.
 * Core entity for RBAC.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotBlank
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Role role;

    @Column(name = "user_type", nullable = false, length = 20)
    private String userType;

    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Enum matching requirements
    public enum Role {
        ROLE_STUDENT, ROLE_OIDB, ROLE_DEAN_OFFICE_STAFF, ROLE_YGK, ROLE_ADMIN
    }
}
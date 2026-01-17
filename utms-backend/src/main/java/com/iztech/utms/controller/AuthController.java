package com.iztech.utms.controller;

import com.iztech.utms.model.User;
import com.iztech.utms.repository.UserRepository;
import com.iztech.utms.repository.AuditLogRepository;
import com.iztech.utms.model.AuditLog;
import com.iztech.utms.model.ActionType;
import com.iztech.utms.security.JwtUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Handles UC-SYS-01 (Login).
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final JwtUtils jwtUtils;
    private final com.iztech.utms.service.AuthService authService;
    private final com.iztech.utms.repository.AdministrativeProfileRepository administrativeProfileRepository;
    private final com.iztech.utms.repository.StudentProfileRepository studentProfileRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate (SEC-03 BCrypt check happens inside authenticationManager)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().stream().findFirst().get().getAuthority();

            // Log Successful Login
            auditLogRepository.save(AuditLog.builder()
                    .actorUsername(userDetails.getUsername())
                    .actionType(ActionType.LOGIN_SUCCESS)
                    .details("User logged in successfully.")
                    .targetApplicationId(0L) // System-level event
                    .build());

            // Fetch Admin Profile if exists
            com.iztech.utms.model.AdministrativeProfile profile = administrativeProfileRepository.findById(
                    userRepository.findByUsername(userDetails.getUsername()).get().getId()).orElse(null);

            Integer deptId = (profile != null && profile.getDepartment() != null) ? profile.getDepartment().getId()
                    : null;
            Integer facultyId = (profile != null && profile.getFaculty() != null) ? profile.getFaculty().getId() : null;

            String scopeName = null;
            if (profile != null) {
                if (profile.getDepartment() != null) {
                    scopeName = profile.getDepartment().getName();
                } else if (profile.getFaculty() != null) {
                    scopeName = profile.getFaculty().getName();
                }
            }

            return ResponseEntity
                    .ok(new JwtResponse(jwt, userDetails.getUsername(), role, deptId, facultyId, scopeName));

        } catch (org.springframework.security.core.AuthenticationException e) {
            // Log Failed Login
            auditLogRepository.save(AuditLog.builder()
                    .actorUsername(loginRequest.getUsername() != null ? loginRequest.getUsername() : "UNKNOWN")
                    .actionType(ActionType.LOGIN_FAILED)
                    .details("Failed login attempt for username: " + loginRequest.getUsername())
                    .targetApplicationId(0L) // System-level event
                    .build());

            throw e;
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok("If an account exists for this email, a reset link has been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.completePasswordReset(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Password reset successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(@Valid @RequestBody com.iztech.utms.payload.RegisterRequest request) {
        // Check username uniqueness
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        // Check email uniqueness
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        // Check TCKN uniqueness
        if (studentProfileRepository.findByTckn(request.getTckn()).isPresent()) {
            return ResponseEntity.badRequest().body("TCKN already registered");
        }

        // Create user with ROLE_STUDENT
        User user = User.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.ROLE_STUDENT)
                .userType("Student")
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        // Create StudentProfile with TCKN only
        com.iztech.utms.model.StudentProfile profile = new com.iztech.utms.model.StudentProfile();
        profile.setUser(savedUser);
        profile.setTckn(request.getTckn());
        studentProfileRepository.save(profile);

        // Log registration
        auditLogRepository.save(AuditLog.builder()
                .actorUsername(savedUser.getUsername())
                .actionType(ActionType.USER_CREATE)
                .details("Student self-registered")
                .targetApplicationId(0L)
                .build());

        return ResponseEntity.ok("Registration successful. Please login with your credentials.");
    }

    // DTOs for Auth
    @Data
    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
    }

    @Data
    public static class ForgotPasswordRequest {
        @NotBlank
        private String email;
    }

    @Data
    public static class ResetPasswordRequest {
        @NotBlank
        private String token;
        @NotBlank
        private String newPassword;
    }

    @Data
    @AllArgsConstructor
    public static class JwtResponse {
        private String token;
        private String username;
        private String role;
        private Integer departmentId;
        private Integer facultyId;
        private String scopeName;
        private final String type = "Bearer";
    }
}
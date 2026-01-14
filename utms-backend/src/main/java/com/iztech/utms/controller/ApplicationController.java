package com.iztech.utms.controller;

import com.iztech.utms.dto.ApplicationDTO;
import com.iztech.utms.model.Application.ApplicationStatus;
import com.iztech.utms.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    // UC-STU-01: Student Submit
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> submitApplication(@Valid @RequestBody ApplicationDTO.Request request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            ApplicationDTO.Response response = applicationService.submitApplication(username, request);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // UC-STU-02: Get My Application
    @GetMapping("/my-application")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getMyApplication() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            ApplicationDTO.Response response = applicationService.getMyApplication(username);
            if (response == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Checking YKS Score (Helper for "Retrieve Score" button)
    @GetMapping("/my-yks-score")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getMyYksScore() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            java.math.BigDecimal score = applicationService.retrieveYksScore(username);
            return ResponseEntity.ok(java.util.Collections.singletonMap("score", score));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // WP-4 ADDITION: OIDB View Incoming
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('OIDB', 'DEAN', 'YGK')")
    public ResponseEntity<?> getApplicationsByStatus(@PathVariable String status) {
        try {
            ApplicationStatus appStatus = ApplicationStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(applicationService.getApplicationsByStatus(appStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // WP-4 ADDITION: OIDB Forward
    @PatchMapping("/{id}/forward")
    @PreAuthorize("hasRole('OIDB')")
    public ResponseEntity<?> forwardApplication(@PathVariable Long id) {
        try {
            applicationService.forwardApplication(id);
            return ResponseEntity.ok("Application forwarded to Faculty.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // WP-4 ADDITION: OIDB Return
    @PatchMapping("/{id}/return")
    @PreAuthorize("hasRole('OIDB')")
    public ResponseEntity<?> returnApplication(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String reason = payload.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Return reason is required.");
            }
            applicationService.returnApplication(id, reason);
            return ResponseEntity.ok("Application returned to Student.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // UC-DEAN-01: Assign to YGK
    @PatchMapping("/{id}/assign-ygk")
    @PreAuthorize("hasRole('DEAN')")
    public ResponseEntity<?> assignToYgk(@PathVariable Long id) {
        try {
            applicationService.assignToYgk(id);
            return ResponseEntity.ok("Application assigned to YGK (Under Review).");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // UC-DEAN-02: Approve Final
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('DEAN')")
    public ResponseEntity<?> approveApplication(@PathVariable Long id) {
        try {
            applicationService.approveApplication(id);
            return ResponseEntity.ok("Application Approved.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
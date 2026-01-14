package com.iztech.utms.controller;

import com.iztech.utms.dto.ApplicationDTO;
import com.iztech.utms.service.ApplicationService; // For fetching list
import com.iztech.utms.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    // UC-YGK-01: Submit Evaluation Decision
    @PostMapping("/{appId}")
    @PreAuthorize("hasRole('YGK')") // RBAC
    public ResponseEntity<?> submitEvaluation(
            @PathVariable Long appId,
            @RequestBody Map<String, Object> payload) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            boolean isEligible = (boolean) payload.get("isEligible");
            String note = (String) payload.get("note");

            evaluationService.submitEvaluation(username, appId, isEligible, note);
            return ResponseEntity.ok("Evaluation saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // WP-4 ADDITION: Trigger Verification (Manually by OIDB)
    @PostMapping("/verify/{appId}")
    @PreAuthorize("hasAnyRole('OIDB', 'YGK')")
    public ResponseEntity<?> verifyApplication(@PathVariable Long appId) {
        try {
            evaluationService.verifyStudentData(appId);
            return ResponseEntity.ok("Verification completed.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // UC-YGK-01: Get Ranked List (Draft)
    @GetMapping("/ranking/{deptId}")
    @PreAuthorize("hasAnyRole('YGK', 'DEAN')")
    public ResponseEntity<?> getRanking(@PathVariable Integer deptId) {
        try {
            return ResponseEntity.ok(evaluationService.generateRanking(deptId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // WP-5 Helper: Seed Data for Ranking Test
    @PostMapping("/ranking/seed/{deptId}")
    @PreAuthorize("hasAnyRole('YGK', 'DEAN', 'ADMIN')")
    public ResponseEntity<?> seedRankingData(@PathVariable Integer deptId) {
        try {
            evaluationService.seedRankingData(deptId);
            return ResponseEntity.ok("Draft data seeded for Department " + deptId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
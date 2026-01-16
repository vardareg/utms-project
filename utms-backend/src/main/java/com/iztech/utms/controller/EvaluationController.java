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

import com.iztech.utms.service.EvaluationService.RankingResponse;
import com.iztech.utms.service.ExportService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.ByteArrayInputStream;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final Map<String, ExportService> exportServices;

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
    @PreAuthorize("hasAnyRole('YGK', 'DEAN_OFFICE_STAFF')")
    public ResponseEntity<?> getRanking(@PathVariable Integer deptId) {
        try {
            return ResponseEntity.ok(evaluationService.generateRanking(deptId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // WP-5 Helper: Seed Data for Ranking Test
    // WP-5 Helper: Seed Data for Ranking Test
    @PostMapping("/ranking/seed/{deptId}")
    @PreAuthorize("hasAnyRole('YGK', 'DEAN_OFFICE_STAFF', 'ADMIN')")
    public ResponseEntity<?> seedRankingData(@PathVariable Integer deptId) {
        try {
            evaluationService.seedRankingData(deptId);
            return ResponseEntity.ok("Draft data seeded for Department " + deptId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // UC-YGK-01: Export Ranked List (PDF/Excel)
    @GetMapping("/ranking/{deptId}/export")
    @PreAuthorize("hasAnyRole('YGK', 'DEAN_OFFICE_STAFF', 'OIDB', 'ADMIN')")
    public ResponseEntity<?> exportRanking(
            @PathVariable Integer deptId,
            @RequestParam(defaultValue = "pdf") String format) {

        try {
            RankingResponse data = evaluationService.generateRanking(deptId);
            String serviceName = format.toLowerCase() + "ExportService"; // pdfExportService or excelExportService
            ExportService exportService = exportServices.get(serviceName);

            if (exportService == null) {
                return ResponseEntity.badRequest().body("Unsupported format: " + format);
            }

            ByteArrayInputStream stream = exportService.export(data);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=ranking_" + deptId + "." + format);

            MediaType mediaType = format.equalsIgnoreCase("pdf") ? MediaType.APPLICATION_PDF
                    : MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(mediaType)
                    .body(new InputStreamResource(stream));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Export failed: " + e.getMessage());
        }
    }
}
package com.iztech.utms.service;

import com.iztech.utms.dto.ApplicationDTO;
import com.iztech.utms.model.Application;
import com.iztech.utms.model.Application.ApplicationStatus;
import com.iztech.utms.model.Evaluation;
import com.iztech.utms.model.UniversityStructure.Department;
import com.iztech.utms.model.User;
import com.iztech.utms.repository.ApplicationRepository;
import com.iztech.utms.repository.DepartmentRepository;
import com.iztech.utms.repository.EvaluationRepository;
import com.iztech.utms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final com.iztech.utms.repository.StudentProfileRepository studentProfileRepository;
    private final com.iztech.utms.repository.AuditLogRepository auditLogRepository;
    private final com.iztech.utms.service.UbysService ubysService;
    private final com.iztech.utms.service.StudentService studentService; // Assuming access to student profile via
                                                                         // service or repo

    // UC-YGK-01: Evaluate Single Application
    @Transactional
    public void submitEvaluation(String username, Long applicationId, boolean isEligible, String note) {
        User ygkMember = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // Save Evaluation Record
        Evaluation evaluation = Evaluation.builder()
                .application(app)
                .ygkMember(ygkMember)
                .isEligible(isEligible)
                .decisionNote(note)
                .build();

        evaluationRepository.save(evaluation);

        // Update Application Status
        if (!isEligible) {
            app.setStatus(ApplicationStatus.REJECTED);
        } else {
            // Eligible students move to UNDER_REVIEW until the final ranking is generated
            app.setStatus(ApplicationStatus.UNDER_REVIEW);
        }
        applicationRepository.save(app);

        // Audit Log: EVALUATE
        auditLogRepository.save(com.iztech.utms.model.AuditLog.builder()
                .actorUsername(username)
                .actionType(com.iztech.utms.model.ActionType.EVALUATE)
                .targetApplicationId(app.getId())
                .details("Evaluation Submitted. Eligible: " + isEligible + ". Note: " + note)
                .build());

    }

    // Verify Student Data with UBYS
    @Transactional
    public void verifyStudentData(Long applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        com.iztech.utms.model.StudentProfile profile = studentProfileRepository.findById(app.getStudent().getId())
                .orElseThrow(() -> new RuntimeException("Student Profile not found"));

        String tckn = profile.getTckn();
        List<com.iztech.utms.dto.TranscriptDto> transcripts = ubysService.getStudentTranscripts(tckn);

        // Audit Log: EXTERNAL_READ
        auditLogRepository.save(com.iztech.utms.model.AuditLog.builder()
                .actorUsername("SYSTEM")
                .actionType(com.iztech.utms.model.ActionType.VIEW)
                .targetApplicationId(app.getId())
                .details("EXTERNAL_READ: Fetched Transcripts from UBYS for verification.")
                .build());

        if (transcripts.isEmpty()) {
            app.setDataVerificationStatus("UBYS_NO_DATA");
        } else {
            // Calculate Logic: For now just marking as Verified if data exists
            // Real logic would compare GPA.

            // Calculate Mock GPA (Simple average)
            BigDecimal totalGrade = BigDecimal.ZERO;
            int totalCredit = 0;
            for (com.iztech.utms.dto.TranscriptDto t : transcripts) {
                totalGrade = totalGrade.add(t.getGrade().multiply(new BigDecimal(t.getCredit())));
                totalCredit += t.getCredit();
            }

            if (totalCredit > 0) {
                BigDecimal calculatedGpa = totalGrade.divide(new BigDecimal(totalCredit), 2,
                        java.math.RoundingMode.HALF_UP);
                BigDecimal studentGpa = profile.getOverallGpa();

                // Allow small difference due to calculation methods
                if (calculatedGpa.subtract(studentGpa).abs().compareTo(new BigDecimal("0.1")) > 0) {
                    app.setDataVerificationStatus("GPA_MISMATCH (calc: " + calculatedGpa + ")");
                } else {
                    app.setDataVerificationStatus("VERIFIED");
                }
            } else {
                app.setDataVerificationStatus("UBYS_EMPTY_TRANSCRIPT");
            }
        }

        applicationRepository.save(app);
    }

    // UC-YGK-01 / PR-10: Generate Ranked List
    public RankingResponse generateRanking(Integer departmentId) {
        Department dept = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        // 1. Fetch all applications for this department, Sorted by Score (PR-07)
        List<Application> allApps = applicationRepository
                .findByTargetDepartmentIdOrderByCompositeScoreDesc(departmentId);

        // 2. Filter only Eligible applications (Status: UNDER_REVIEW or APPROVED)
        // We exclude REJECTED, RETURNED, NEW, FORWARDED
        List<Application> eligibleApps = allApps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.UNDER_REVIEW
                        || a.getStatus() == ApplicationStatus.APPROVED)
                .collect(Collectors.toList());

        List<RankingDTO> primaryList = new ArrayList<>();
        List<RankingDTO> waitList = new ArrayList<>();
        int quota = dept.getQuota(); // PR-08

        // 3. Apply Ranking Logic (PR-10)
        int waitListQuota = (int) Math.ceil(dept.getQuota() * 0.5);

        for (int i = 0; i < eligibleApps.size(); i++) {
            Application app = eligibleApps.get(i);
            RankingDTO dto = mapToRankingDTO(app, i + 1);

            if (i < quota) {
                primaryList.add(dto);
            } else if (i < quota + waitListQuota) {
                waitList.add(dto);
            } else {
                // Remaining candidates are cut off (implicitly Rejected for this list)
            }
        }

        return new RankingResponse(dept.getName(), quota, primaryList, waitList);
    }

    private RankingDTO mapToRankingDTO(Application app, int rank) {
        return new RankingDTO(
                rank,
                app.getId(),
                app.getStudent().getUsername(), // Mask in prod
                app.getCompositeScore(),
                app.getConvertedGpa(),
                app.getYksScore());
    }

    // DTOs for Ranking
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class RankingResponse {
        private String departmentName;
        private int quota;
        private List<RankingDTO> primaryList; // Asil
        private List<RankingDTO> waitList; // Yedek
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class RankingDTO {
        private int rank;
        private Long trackingId;
        private String studentName;
        private java.math.BigDecimal compositeScore;
        private java.math.BigDecimal gpa;
        private java.math.BigDecimal yks;
    }
}
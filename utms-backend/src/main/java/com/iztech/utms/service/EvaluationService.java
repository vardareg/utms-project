package com.iztech.utms.service;

import com.iztech.utms.model.Application;
import com.iztech.utms.model.Application.ApplicationStatus;
import com.iztech.utms.model.Evaluation;
import com.iztech.utms.model.UniversityStructure.Department;
import com.iztech.utms.model.User;
import com.iztech.utms.repository.ApplicationRepository;
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
    private final com.iztech.utms.repository.DepartmentRepository departmentRepository;
    private final com.iztech.utms.repository.AdministrativeProfileRepository administrativeProfileRepository;
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

        // ACCESS CONTROL: Link YGK User to Department
        com.iztech.utms.model.AdministrativeProfile profile = administrativeProfileRepository
                .findById(ygkMember.getId())
                .orElse(null);

        if (profile != null) {
            if (profile.getDepartment() != null
                    && !profile.getDepartment().getId().equals(app.getTargetDepartment().getId())) {
                throw new RuntimeException(
                        "Access Denied: You are authorized for " + profile.getDepartment().getName() + " only.");
            }
            if (profile.getFaculty() != null
                    && !profile.getFaculty().getId().equals(app.getTargetDepartment().getFaculty().getId())) {
                throw new RuntimeException(
                        "Access Denied: You are authorized for " + profile.getFaculty().getName() + " only.");
            }
        }

        if (app.getStatus() != ApplicationStatus.UNDER_REVIEW) {
            throw new RuntimeException(
                    "Action Restricted: Application must be assigned by Dean (UNDER_REVIEW) before evaluation.");
        }

        // Save or Update Evaluation Record (Draft Mode)
        Evaluation evaluation = evaluationRepository.findByApplicationId(applicationId)
                .orElse(Evaluation.builder()
                        .application(app)
                        .ygkMember(ygkMember)
                        .build());

        evaluation.setEligible(isEligible);
        evaluation.setDecisionNote(note);

        evaluationRepository.save(evaluation);

        // DO NOT Change Application Status here. It remains UNDER_REVIEW.
        // The decision is stored in the Evaluation record and exposed via DTO as
        // 'ygkDecision'.
        // Finalization happens in finalizeEvaluations().

        // Audit Log: EVALUATE (Draft)
        auditLogRepository.save(com.iztech.utms.model.AuditLog.builder()
                .actorUsername(username)
                .actionType(com.iztech.utms.model.ActionType.EVALUATE)
                .targetApplicationId(app.getId())
                .details("Draft Evaluation for " + app.getStudent().getUsername() + ": "
                        + (isEligible ? "ELIGIBLE" : "NOT_ELIGIBLE"))
                .build());

    }

    // UC-YGK-0X: Finalize Evaluations (Transition to FINALIZED for Dean Approval)
    @Transactional
    public void finalizeEvaluations(Integer departmentId) {
        List<Application> pendingApps = applicationRepository.findByTargetDepartmentIdAndStatus(departmentId,
                ApplicationStatus.UNDER_REVIEW);

        if (pendingApps.isEmpty()) {
            throw new RuntimeException("No pending evaluations to finalize for this department.");
        }

        int finalizedCount = 0;
        int rejectedCount = 0;

        for (Application app : pendingApps) {
            // Fetch Draft Decision
            java.util.Optional<Evaluation> evalOpt = evaluationRepository.findByApplicationId(app.getId());

            if (evalOpt.isPresent()) {
                Evaluation eval = evalOpt.get();
                if (eval.isEligible()) {
                    app.setStatus(ApplicationStatus.FINALIZED);
                    finalizedCount++;
                } else {
                    app.setStatus(ApplicationStatus.REJECTED);
                    rejectedCount++;
                }
                applicationRepository.save(app);
            }
            // If no evaluation exists, keep UNDER_REVIEW (Pending)
        }

        // Log
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();

        auditLogRepository.save(com.iztech.utms.model.AuditLog.builder()
                .actorUsername(currentUsername)
                .actionType(com.iztech.utms.model.ActionType.APPROVE)
                .targetApplicationId(0L)
                .details("YGK Finalized Department " + departmentId + ". Finalized: " + finalizedCount + ", Rejected: "
                        + rejectedCount)
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

        // ACCESS CONTROL Check (Similar to ApplicationService)
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Skip for ADMIN
        if (currentUser.getRole() != User.Role.ROLE_ADMIN && currentUser.getRole() != User.Role.ROLE_OIDB) {
            com.iztech.utms.model.AdministrativeProfile profile = administrativeProfileRepository
                    .findById(currentUser.getId()).orElse(null);
            if (profile != null) {
                if (profile.getDepartment() != null && !profile.getDepartment().getId().equals(departmentId)) {
                    throw new RuntimeException("Access Denied: You cannot view rankings for other departments.");
                }
                if (profile.getFaculty() != null && !dept.getFaculty().getId().equals(profile.getFaculty().getId())) {
                    throw new RuntimeException("Access Denied: You cannot view rankings for other faculties.");
                }
            }
        }

        // 1. Fetch all applications for this department, Sorted by Score (PR-07)
        // Sort Order: Composite Score (Desc) -> YKS Score (Desc) -> GPA (Desc) ->
        // Submission Date (Asc)
        List<Application> allApps = applicationRepository
                .findByTargetDepartmentIdOrderByCompositeScoreDescYksScoreDescConvertedGpaDescSubmissionDateAsc(
                        departmentId);

        // 2. Filter only Eligible applications
        // We include FINALIZED/APPROVED (Confirmed Eligible)
        // For UNDER_REVIEW, we check if there is a Draft Evaluation that says
        // "Eligible".
        List<Application> eligibleApps = allApps.stream()
                .filter(a -> {
                    if (a.getStatus() == ApplicationStatus.FINALIZED || a.getStatus() == ApplicationStatus.APPROVED) {
                        return true;
                    }
                    if (a.getStatus() == ApplicationStatus.UNDER_REVIEW) {
                        // Check Draft Evaluation
                        return evaluationRepository.findByApplicationId(a.getId())
                                .map(Evaluation::isEligible)
                                .orElse(false); // Default to false (Pending = Not yet eligible for ranking)
                    }
                    return false;
                })
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
        User student = app.getStudent();
        String fullName = student.getFirstName() + " " + student.getLastName();
        return new RankingDTO(
                rank,
                app.getId(),
                fullName,
                app.getCompositeScore(),
                app.getConvertedGpa(),
                app.getYksScore());
    }

    // Helper for Manual Verification
    @Transactional
    public void seedRankingData(Integer departmentId) {
        Department dept = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found. Ensure ID 1 exists."));

        // Helper to create app
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.util.Random random = new java.util.Random();

        for (int i = 1; i <= 15; i++) {
            String studentUsername = "student_" + departmentId + "_" + i;

            // Randomize Scores
            // YKS: 200 - 550
            double yksVal = 200 + (350 * random.nextDouble());
            BigDecimal yks = new BigDecimal(yksVal).setScale(2, java.math.RoundingMode.HALF_UP);

            // GPA: 2.00 - 4.00
            double gpaVal = 2.0 + (2.0 * random.nextDouble());
            BigDecimal gpa = new BigDecimal(gpaVal).setScale(2, java.math.RoundingMode.HALF_UP);

            // Composite: (YKS * 0.5) + (GPA * 100 * 0.5) approx formula for variation
            BigDecimal composite = yks.multiply(new BigDecimal("0.5"))
                    .add(gpa.multiply(new BigDecimal("100")).multiply(new BigDecimal("0.5")))
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            createSeededApp(studentUsername, departmentId, composite, yks, gpa, now.plusMinutes(random.nextInt(120)));
        }
    }

    private void createSeededApp(String username, Integer deptId, BigDecimal score, BigDecimal yks, BigDecimal gpa,
            java.time.LocalDateTime date) {
        User student = userRepository.findByUsername(username).orElseGet(() -> {
            User u = new User();
            u.setUsername(username);
            u.setEmail(username + "@std.iztech.edu.tr");
            u.setPasswordHash("{noop}password"); // Simple hash for dev
            u.setRole(com.iztech.utms.model.User.Role.ROLE_STUDENT);
            u.setUserType("STUDENT");
            u.setEnabled(true);
            return userRepository.save(u);
        });

        // Check if app exists
        if (applicationRepository.findByStudentIdAndTargetDepartmentId(student.getId(), deptId).isPresent())
            return;

        Application app = new Application();
        app.setStudent(student);
        app.setTargetDepartment(departmentRepository.findById(deptId).get());
        app.setCompositeScore(score);
        app.setYksScore(yks);
        app.setConvertedGpa(gpa);
        app.setSubmissionDate(date);
        app.setStatus(ApplicationStatus.UNDER_REVIEW);
        app.setDataVerificationStatus("VERIFIED");
        applicationRepository.save(app);
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
        private String fullName;
        private java.math.BigDecimal compositeScore;
        private java.math.BigDecimal gpa;
        private java.math.BigDecimal yks;
    }
}
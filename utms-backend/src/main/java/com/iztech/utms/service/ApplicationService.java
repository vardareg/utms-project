package com.iztech.utms.service;

import com.iztech.utms.dto.ApplicationDTO;
import com.iztech.utms.model.Application;
import com.iztech.utms.model.Application.ApplicationStatus;
import com.iztech.utms.model.Document;
import com.iztech.utms.model.StudentProfile;
import com.iztech.utms.model.UniversityStructure.Department;
import com.iztech.utms.model.User;
import com.iztech.utms.repository.ApplicationRepository;
import com.iztech.utms.repository.DepartmentRepository;
import com.iztech.utms.repository.StudentProfileRepository;
import com.iztech.utms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final ScoringService scoringService;

    private static final BigDecimal MIN_GPA_THRESHOLD = new BigDecimal("2.50");

    @Transactional
    public ApplicationDTO.Response submitApplication(String username, ApplicationDTO.Request request) {
        User studentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(studentUser.getId())
                .orElseThrow(() -> new RuntimeException("Student Profile not found. Please contact OIDB."));

        Department department = departmentRepository.findById(request.getTargetDepartmentId())
                .orElseThrow(() -> new RuntimeException("Invalid Department ID"));

        if (profile.getOverallGpa().compareTo(MIN_GPA_THRESHOLD) < 0) {
            throw new RuntimeException("Eligibility Error: Your GPA (" + profile.getOverallGpa() +
                    ") is below the minimum required (" + MIN_GPA_THRESHOLD + ").");
        }

        boolean exists = applicationRepository.existsByStudentIdAndTargetDepartmentId(studentUser.getId(),
                department.getId());
        if (exists) {
            throw new RuntimeException("Validation Error: You have already applied to " + department.getName());
        }

        // Apply Scoring Logic
        BigDecimal convertedGpa = scoringService.convertGpaTo100(profile.getOverallGpa());
        BigDecimal compositeScore = scoringService.calculateCompositeScore(convertedGpa, request.getYksScore());

        Application application = Application.builder()
                .student(studentUser)
                .targetDepartment(department)
                .yksScore(request.getYksScore())
                .convertedGpa(convertedGpa) // STORED AS 100-SCALE
                .compositeScore(compositeScore)
                .status(ApplicationStatus.NEW)
                .build();

        Application savedApp = applicationRepository.save(application);

        return mapToResponse(savedApp);
    }

    // WP-4 ADDITION: Fetch Applications by Status (e.g., NEW for OIDB)
    public List<ApplicationDTO.Response> getApplicationsByStatus(ApplicationStatus status) {
        return applicationRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // WP-4 ADDITION: Forward to Faculty
    @Transactional
    public void forwardApplication(Long applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (app.getStatus() != ApplicationStatus.NEW && app.getStatus() != ApplicationStatus.RESUBMITTED) {
            throw new RuntimeException("Invalid Status Transition: Can only forward NEW or RESUBMITTED applications.");
        }

        // PR-03 Enforcement: Check for English Proof
        boolean hasEnglishProof = app.getDocuments() != null && app.getDocuments().stream()
                .anyMatch(d -> d.getDocumentType() == Document.DocumentType.ENGLISH_PROOF);

        if (!hasEnglishProof) {
            throw new RuntimeException(
                    "Missing Document: English Proof of Proficiency is required to forward the application.");
        }

        app.setStatus(ApplicationStatus.FORWARDED);
        applicationRepository.save(app);
    }

    // WP-4 ADDITION: Return to Student
    @Transactional
    public void returnApplication(Long applicationId, String reason) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        app.setStatus(ApplicationStatus.RETURNED);
        app.setReturnReason(reason);
        applicationRepository.save(app);
    }

    // UC-DEAN-01: Dean Assigns to YGK
    @Transactional
    public void assignToYgk(Long applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (app.getStatus() != ApplicationStatus.FORWARDED) {
            throw new RuntimeException("Invalid Status: Can only assign FORWARDED applications.");
        }

        app.setStatus(ApplicationStatus.UNDER_REVIEW);
        applicationRepository.save(app);
    }

    // UC-DEAN-02: Dean Approves Application (Final Step)
    @Transactional
    public void approveApplication(Long applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // Approving either from UNDER_REVIEW (Direct) or FINALIZED (Ranked)
        if (app.getStatus() != ApplicationStatus.UNDER_REVIEW && app.getStatus() != ApplicationStatus.FINALIZED) {
            throw new RuntimeException("Invalid Status: Can only approve UNDER_REVIEW or FINALIZED applications.");
        }

        app.setStatus(ApplicationStatus.APPROVED);
        applicationRepository.save(app);
    }

    private ApplicationDTO.Response mapToResponse(Application app) {
        ApplicationDTO.Response response = new ApplicationDTO.Response();
        response.setTrackingId(app.getId());
        // For privacy, we might mask name, but OIDB needs it.
        response.setStudentName(app.getStudent().getUsername());
        response.setStatus(app.getStatus().name());
        response.setDepartmentName(app.getTargetDepartment().getName());
        response.setCompositeScore(app.getCompositeScore());
        response.setYksScore(app.getYksScore());
        response.setGpa(app.getConvertedGpa());
        response.setSubmissionDate(app.getSubmissionDate().toString());

        // Map Documents
        if (app.getDocuments() != null) {
            List<ApplicationDTO.DocumentResponse> docs = app.getDocuments().stream()
                    .map(doc -> {
                        ApplicationDTO.DocumentResponse d = new ApplicationDTO.DocumentResponse();
                        d.setId(doc.getId());
                        d.setType(doc.getDocumentType().name());
                        // Extract filename from path or use generic
                        d.setFileName(java.nio.file.Paths.get(doc.getFilePath()).getFileName().toString());
                        return d;
                    })
                    .collect(java.util.stream.Collectors.toList());
            response.setDocuments(docs);
        }

        return response;
    }
}
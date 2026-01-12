package main.java.com.iztech.utms.service;

// ... existing imports ...
// This file updates the mapToResponse method in the existing Service to populate documents.

/* NOTE: Please replace the existing mapToResponse method 
   in src/main/java/com/iztech/utms/service/ApplicationService.java 
   with this updated version.
*/


import com.iztech.utms.dto.ApplicationDTO;
import com.iztech.utms.model.Application;
import com.iztech.utms.model.Application.ApplicationStatus;
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
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

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

        boolean exists = applicationRepository.existsByStudentIdAndTargetDepartmentId(studentUser.getId(), department.getId());
        if (exists) {
            throw new RuntimeException("Validation Error: You have already applied to " + department.getName());
        }

        BigDecimal compositeScore = calculateCompositeScore(profile.getOverallGpa(), request.getYksScore());

        Application application = Application.builder()
                .student(studentUser)
                .targetDepartment(department)
                .yksScore(request.getYksScore())
                .convertedGpa(profile.getOverallGpa())
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

    private BigDecimal calculateCompositeScore(BigDecimal gpa, BigDecimal yks) {
        BigDecimal gpaPart = gpa.multiply(new BigDecimal("0.5"));
        BigDecimal yksPart = yks.multiply(new BigDecimal("0.5"));
        return gpaPart.add(yksPart).setScale(3, RoundingMode.HALF_UP);
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
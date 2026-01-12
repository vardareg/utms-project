package main.java.com.iztech.utms.service;

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
    }

    // UC-YGK-01 / PR-10: Generate Ranked List
    public RankingResponse generateRanking(Integer departmentId) {
        Department dept = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        // 1. Fetch all applications for this department, Sorted by Score (PR-07)
        List<Application> allApps = applicationRepository.findByTargetDepartmentIdOrderByCompositeScoreDesc(departmentId);

        // 2. Filter only Eligible applications (Status: UNDER_REVIEW or APPROVED)
        // We exclude REJECTED, RETURNED, NEW, FORWARDED
        List<Application> eligibleApps = allApps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.UNDER_REVIEW || a.getStatus() == ApplicationStatus.APPROVED)
                .collect(Collectors.toList());

        List<RankingDTO> primaryList = new ArrayList<>();
        List<RankingDTO> waitList = new ArrayList<>();
        int quota = dept.getQuota(); // PR-08

        // 3. Apply Ranking Logic (PR-10)
        for (int i = 0; i < eligibleApps.size(); i++) {
            Application app = eligibleApps.get(i);
            RankingDTO dto = mapToRankingDTO(app, i + 1);

            if (i < quota) {
                primaryList.add(dto);
            } else {
                // Waitlist logic: Usually quota * 0.5 or similar. 
                // For MVP, everyone else is waitlisted.
                waitList.add(dto);
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
                app.getYksScore()
        );
    }

    // DTOs for Ranking
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class RankingResponse {
        private String departmentName;
        private int quota;
        private List<RankingDTO> primaryList; // Asil
        private List<RankingDTO> waitList;    // Yedek
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
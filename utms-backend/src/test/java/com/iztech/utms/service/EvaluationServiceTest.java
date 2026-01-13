package com.iztech.utms.service;

import com.iztech.utms.model.Application;
import com.iztech.utms.model.Application.ApplicationStatus;
import com.iztech.utms.model.UniversityStructure.Department;
import com.iztech.utms.model.User;
import com.iztech.utms.repository.ApplicationRepository;
import com.iztech.utms.repository.DepartmentRepository;
import com.iztech.utms.repository.EvaluationRepository;
import com.iztech.utms.repository.UserRepository;
import com.iztech.utms.service.EvaluationService.RankingResponse;
import com.iztech.utms.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class EvaluationServiceTest {

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private EvaluationService evaluationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateRanking_WaitlistLogic() {
        // Setup Department with Quota = 10
        Department dept = new Department();
        dept.setId(1);
        dept.setName("Computer Engineering");
        dept.setQuota(10); // Waitlist should be ceil(10 * 0.5) = 5

        when(departmentRepository.findById(1)).thenReturn(Optional.of(dept));

        // Create 20 eligible applications
        List<Application> apps = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Application app = new Application();
            app.setId((long) i);
            app.setStatus(ApplicationStatus.UNDER_REVIEW);
            app.setCompositeScore(new BigDecimal(100 - i)); // Decreasing score

            // Need a dummy student for mapping dto
            User student = new User();
            student.setUsername("student" + i);
            app.setStudent(student);

            apps.add(app);
        }

        when(applicationRepository.findByTargetDepartmentIdOrderByCompositeScoreDesc(1)).thenReturn(apps);

        // Execute
        RankingResponse response = evaluationService.generateRanking(1);

        // Assertions
        assertEquals(10, response.getPrimaryList().size(), "Primary list should match quota");
        assertEquals(5, response.getWaitList().size(), "Waitlist should be 50% of quota");

        // Verify Cutoff (Total accounted for = 15)
        // 20 apps total -> 10 primary + 5 waitlist + 5 discarded
    }

    @Test
    void testGenerateRanking_WaitlistRounding() {
        // Setup Department with Quota = 5
        Department dept = new Department();
        dept.setId(1);
        dept.setName("Software Engineering");
        dept.setQuota(5); // Waitlist should be ceil(5 * 0.5) = ceil(2.5) = 3

        when(departmentRepository.findById(1)).thenReturn(Optional.of(dept));

        List<Application> apps = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Application app = new Application();
            app.setId((long) i);
            app.setStatus(ApplicationStatus.UNDER_REVIEW);
            app.setCompositeScore(new BigDecimal(100 - i));

            User student = new User();
            student.setUsername("student" + i);
            app.setStudent(student);

            apps.add(app);
        }

        when(applicationRepository.findByTargetDepartmentIdOrderByCompositeScoreDesc(1)).thenReturn(apps);

        RankingResponse response = evaluationService.generateRanking(1);

        assertEquals(5, response.getPrimaryList().size());
        assertEquals(3, response.getWaitList().size());
    }
}

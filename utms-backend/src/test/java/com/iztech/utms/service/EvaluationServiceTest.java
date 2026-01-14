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

    @Mock
    private com.iztech.utms.repository.StudentProfileRepository studentProfileRepository;

    @Mock
    private UbysService ubysService;

    @Mock
    private StudentService studentService;

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

        when(applicationRepository
                .findByTargetDepartmentIdOrderByCompositeScoreDescYksScoreDescConvertedGpaDescSubmissionDateAsc(1))
                .thenReturn(apps);

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

        when(applicationRepository
                .findByTargetDepartmentIdOrderByCompositeScoreDescYksScoreDescConvertedGpaDescSubmissionDateAsc(1))
                .thenReturn(apps);

        RankingResponse response = evaluationService.generateRanking(1);

        assertEquals(5, response.getPrimaryList().size());
        assertEquals(3, response.getWaitList().size());
    }

    @Test
    void testGenerateRanking_TieBreaking() {
        Department dept = new Department();
        dept.setId(1);
        dept.setName("Computer Engineering");
        dept.setQuota(10);

        when(departmentRepository.findById(1)).thenReturn(Optional.of(dept));

        List<Application> apps = new ArrayList<>();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        // Candidate A: Score 100, YKS 450, GPA 3.5
        Application appA = createMockApp(1L, "A", new BigDecimal("100"), new BigDecimal("450"), new BigDecimal("3.50"),
                now);
        // Candidate B: Score 100, YKS 440, GPA 3.8
        Application appB = createMockApp(2L, "B", new BigDecimal("100"), new BigDecimal("440"), new BigDecimal("3.80"),
                now);
        // Candidate C: Score 100, YKS 440, GPA 3.5, Date T
        Application appC = createMockApp(3L, "C", new BigDecimal("100"), new BigDecimal("440"), new BigDecimal("3.50"),
                now);
        // Candidate D: Score 100, YKS 440, GPA 3.5, Date T+1
        Application appD = createMockApp(4L, "D", new BigDecimal("100"), new BigDecimal("440"), new BigDecimal("3.50"),
                now.plusSeconds(1));

        apps.add(appA);
        apps.add(appB);
        apps.add(appC);
        apps.add(appD);

        // Mock repo returning them in assumed sorted order (Repo is responsible for
        // sorting, Service just respects it)
        when(applicationRepository
                .findByTargetDepartmentIdOrderByCompositeScoreDescYksScoreDescConvertedGpaDescSubmissionDateAsc(1))
                .thenReturn(apps);

        RankingResponse response = evaluationService.generateRanking(1);

        assertEquals(4, response.getPrimaryList().size());
        assertEquals(appA.getId(), response.getPrimaryList().get(0).getTrackingId()); // A First
        assertEquals(appB.getId(), response.getPrimaryList().get(1).getTrackingId()); // B Second (Lower YKS)
        assertEquals(appC.getId(), response.getPrimaryList().get(2).getTrackingId()); // C Third (Lower GPA)
        assertEquals(appD.getId(), response.getPrimaryList().get(3).getTrackingId()); // D Fourth (Later Date)
    }

    private Application createMockApp(Long id, String username, BigDecimal score, BigDecimal yks, BigDecimal gpa,
            java.time.LocalDateTime date) {
        Application app = new Application();
        app.setId(id);
        app.setStatus(ApplicationStatus.UNDER_REVIEW);
        app.setCompositeScore(score);
        app.setYksScore(yks);
        app.setConvertedGpa(gpa);
        app.setSubmissionDate(date);
        User student = new User();
        student.setUsername(username);
        app.setStudent(student);
        return app;
    }
}

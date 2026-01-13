package com.iztech.utms.service;

import com.iztech.utms.dto.ApplicationDTO;
import com.iztech.utms.model.*;
import com.iztech.utms.model.UniversityStructure.Department;
import com.iztech.utms.repository.ApplicationRepository;
import com.iztech.utms.repository.AuditLogRepository;
import com.iztech.utms.repository.DepartmentRepository;
import com.iztech.utms.repository.StudentProfileRepository;
import com.iztech.utms.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuditLogVerificationTest {

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private StudentProfileRepository studentProfileRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ScoringService scoringService;
    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    public void testSubmitApplication_LogsSubmitAction() {
        // Arrange
        String username = "student1";
        ApplicationDTO.Request request = new ApplicationDTO.Request();
        request.setTargetDepartmentId(1);
        request.setYksScore(new BigDecimal("400"));

        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        StudentProfile profile = new StudentProfile();
        profile.setOverallGpa(new BigDecimal("3.00"));

        Department department = new Department();
        department.setId(1);
        department.setName("CENG");

        Application app = new Application();
        app.setId(100L);
        app.setStudent(user);
        app.setTargetDepartment(department);
        app.setStatus(Application.ApplicationStatus.NEW);
        app.setSubmissionDate(java.time.LocalDateTime.now());
        app.setCompositeScore(BigDecimal.TEN);
        app.setYksScore(BigDecimal.TEN);
        app.setConvertedGpa(BigDecimal.TEN);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(studentProfileRepository.findByUserId(user.getId())).thenReturn(Optional.of(profile));
        when(departmentRepository.findById(1)).thenReturn(Optional.of(department));
        when(applicationRepository.existsByStudentIdAndTargetDepartmentId(any(), any())).thenReturn(false);
        when(scoringService.convertGpaTo100(any())).thenReturn(new BigDecimal("80"));
        when(scoringService.calculateCompositeScore(any(), any())).thenReturn(new BigDecimal("200"));
        when(applicationRepository.save(any(Application.class))).thenReturn(app);

        // Act
        applicationService.submitApplication(username, request);

        // Assert
        ArgumentCaptor<AuditLog> logCaptor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(logCaptor.capture());

        AuditLog capturedLog = logCaptor.getValue();
        assertEquals(username, capturedLog.getActorUsername());
        assertEquals(ActionType.SUBMIT, capturedLog.getActionType());
        assertEquals(100L, capturedLog.getTargetApplicationId());
    }

    @Test
    public void testForwardApplication_LogsForwardAction() {
        // Arrange
        Long appId = 100L;
        Application app = new Application();
        app.setId(appId);
        app.setStatus(Application.ApplicationStatus.NEW);
        // Added english proof to pass validation
        Document doc = new Document();
        doc.setDocumentType(Document.DocumentType.ENGLISH_PROOF);
        app.setDocuments(java.util.Collections.singletonList(doc));

        when(applicationRepository.findById(appId)).thenReturn(Optional.of(app));

        // Act
        applicationService.forwardApplication(appId);

        // Assert
        ArgumentCaptor<AuditLog> logCaptor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(logCaptor.capture());

        AuditLog capturedLog = logCaptor.getValue();
        assertEquals(ActionType.FORWARD, capturedLog.getActionType());
        assertEquals(appId, capturedLog.getTargetApplicationId());
        assertEquals("OIDB", capturedLog.getActorUsername());
    }
}

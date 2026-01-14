package com.iztech.utms.service;

import com.iztech.utms.dto.ApplicationDTO;
import com.iztech.utms.model.StudentProfile;
import com.iztech.utms.model.UniversityStructure.Department;
import com.iztech.utms.model.User;
import com.iztech.utms.repository.ApplicationRepository;
import com.iztech.utms.repository.DepartmentRepository;
import com.iztech.utms.repository.StudentProfileRepository;
import com.iztech.utms.repository.UserRepository;
import com.iztech.utms.repository.DocumentRepository;
import com.iztech.utms.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

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
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private ConfigurationService configurationService; // MOCKED

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    void testSubmitApplication_EligibilityFailure() {
        // Arrange
        String username = "student1";
        ApplicationDTO.Request request = new ApplicationDTO.Request();
        request.setTargetDepartmentId(1); // Integer
        request.setYksScore(new BigDecimal("400"));

        User user = new User();
        user.setId(1L);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        StudentProfile profile = new StudentProfile();
        profile.setOverallGpa(new BigDecimal("2.40")); // Below standard
        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(profile));

        Department dept = new Department();
        dept.setId(1); // Integer
        when(departmentRepository.findById(1)).thenReturn(Optional.of(dept));

        // Mock Dynamic Threshold to 2.50
        when(configurationService.getMinGpaThreshold()).thenReturn(new BigDecimal("2.50"));
        // when(configurationService.getMinYksThreshold()).thenReturn(new
        // BigDecimal("150.00")); // Not strictly needed if GPA fail is first, but good
        // practice.
        // Keeping it commented or skipping unless NPE occurs. The logic fails at GPA
        // first.

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.submitApplication(username, request);
        });

        assertTrue(exception.getMessage().contains("Eligibility Error"));
    }

    @Test
    void testSubmitApplication_EligibilitySuccess_WithDynamicConfig() {
        // Arrange
        String username = "student1";
        ApplicationDTO.Request request = new ApplicationDTO.Request();
        request.setTargetDepartmentId(1); // Integer
        request.setYksScore(new BigDecimal("400"));

        User user = new User();
        user.setId(1L);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        StudentProfile profile = new StudentProfile();
        // If config is 2.50, and GPA is 2.60, should pass eligibility check
        profile.setOverallGpa(new BigDecimal("2.60"));
        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(profile));

        Department dept = new Department();
        dept.setId(1); // Integer
        when(departmentRepository.findById(1)).thenReturn(Optional.of(dept));

        when(configurationService.getMinGpaThreshold()).thenReturn(new BigDecimal("2.50"));
        when(configurationService.getMinYksThreshold()).thenReturn(new BigDecimal("150.00")); // New Mock

        // Mock scoring service used later in method
        when(scoringService.convertGpaTo100(any())).thenReturn(new BigDecimal("80"));
        when(scoringService.calculateCompositeScore(any(), any())).thenReturn(new BigDecimal("240"));
        when(applicationRepository.save(any())).thenAnswer(i -> {
            com.iztech.utms.model.Application app = i.getArgument(0);
            app.setSubmissionDate(java.time.LocalDateTime.now());
            app.setId(1L); // Also set ID for thoroughness
            return app;
        });

        // Act
        applicationService.submitApplication(username, request);

        // Assert
        verify(applicationRepository).save(any());
    }
}

package com.iztech.utms.service;

import com.iztech.utms.dto.StudentProfileDto;
import com.iztech.utms.model.StudentProfile;
import com.iztech.utms.model.User;
import com.iztech.utms.repository.StudentProfileRepository;
import com.iztech.utms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentService(profileRepository, userRepository);
    }

    @Test
    void upsertProfile_NewProfile_WithValidTckn_CreatesProfile() {
        String username = "testuser";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setEmail("test@iztech.edu.tr");

        StudentProfileDto.Request request = StudentProfileDto.Request.builder()
                .tckn("12345678901")
                .currentUniversity("Iztech")
                .currentProgram("CENG")
                .overallGpa(new BigDecimal("3.50"))
                .hasDisciplinaryRecord(false)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(profileRepository.findById(user.getId())).thenReturn(Optional.empty()); // No existing profile
        when(profileRepository.findByTckn("12345678901")).thenReturn(Optional.empty()); // TCKN unique
        when(profileRepository.save(any(StudentProfile.class))).thenAnswer(i -> i.getArguments()[0]);

        StudentProfileDto.Response response = studentService.upsertProfile(username, request);

        ArgumentCaptor<StudentProfile> profileCaptor = ArgumentCaptor.forClass(StudentProfile.class);
        verify(profileRepository).save(profileCaptor.capture());
        StudentProfile saved = profileCaptor.getValue();

        assertEquals("12345678901", saved.getTckn());
        assertEquals("Iztech", saved.getCurrentUniversity());
    }

    @Test
    void upsertProfile_NewProfile_MissingTckn_ThrowsException() {
        String username = "testuser";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        StudentProfileDto.Request request = StudentProfileDto.Request.builder()
                .tckn(null) // Missing TCKN
                .currentUniversity("Iztech")
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(profileRepository.findById(user.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            studentService.upsertProfile(username, request);
        });

        assertTrue(exception.getMessage().contains("TCKN is required"));
        verify(profileRepository, never()).save(any());
    }

    @Test
    void upsertProfile_ExistingProfile_IgnoresTcknUpdate() {
        String username = "testuser";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setEmail("test@iztech.edu.tr");

        StudentProfile existingProfile = StudentProfile.builder()
                .user(user)
                .tckn("11111111111")
                .currentUniversity("Old Uni")
                .build();

        StudentProfileDto.Request request = StudentProfileDto.Request.builder()
                .tckn("99999999999") // Attempt to change TCKN
                .currentUniversity("New Uni")
                .currentProgram("CENG")
                .overallGpa(new BigDecimal("3.00"))
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(profileRepository.findById(user.getId())).thenReturn(Optional.of(existingProfile));
        when(profileRepository.save(any(StudentProfile.class))).thenAnswer(i -> i.getArguments()[0]);

        studentService.upsertProfile(username, request);

        ArgumentCaptor<StudentProfile> profileCaptor = ArgumentCaptor.forClass(StudentProfile.class);
        verify(profileRepository).save(profileCaptor.capture());
        StudentProfile saved = profileCaptor.getValue();

        assertEquals("11111111111", saved.getTckn(), "TCKN should not change");
        assertEquals("New Uni", saved.getCurrentUniversity());
    }
}

package com.iztech.utms.service;

import com.iztech.utms.model.Application;
import com.iztech.utms.model.Document;
import com.iztech.utms.model.User;
import com.iztech.utms.repository.ApplicationRepository;
import com.iztech.utms.repository.DocumentRepository;
import com.iztech.utms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FileStorageService fileStorageService;

    private User student;
    private Application application;

    @BeforeEach
    void setUp() {
        student = new User();
        student.setUsername("testuser");

        application = new Application();
        application.setId(1L);
        application.setStudent(student);
    }

    @Test
    void storeDocument_FileSizeExceedsLimit_ShouldThrowException() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/pdf");
        // 5MB + 1 byte
        when(file.getSize()).thenReturn(5 * 1024 * 1024 + 1L);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> fileStorageService.storeDocument("testuser", 1L, "TRANSCRIPT", file));

        assertTrue(exception.getMessage().contains("File size exceeds the 5MB limit (PR-11)"));
        verify(documentRepository, never()).save(any(Document.class));
    }

    @Test
    void storeDocument_ValidFileSize_ShouldProceed() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getSize()).thenReturn(1024L); // 1KB

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        // We need to mock the actual file copy or ensure it doesn't fail.
        // However, FileStorageService uses Files.copy(inputStream, path).
        // Mocking static Files methods is hard without PowerMock.
        // But the service copies from file.getInputStream().
        // If we want to unit test the logic fully without actual IO, we might hit the
        // IO part.

        // Wait, the requirement was to ensure the check runs BEFORE saving to disk.
        // The exception in the previous test confirms the check runs before any repo
        // save or file copy
        // (assuming the check is placed before).

        // For the success case, we might encounter IOException if we don't mock
        // getInputStream.
        // But verify the size check passes is enough if we primarily care about the
        // logic location.
        // Let's rely on the previous test for the blocking behavior.
        // For this test, let's just ensure it DOESN'T throw the size exception.
        // Actually, if I let it proceed to applicationRepository.findById, and mock
        // that,
        // eventually it will try to write to disk.
        // Ideally we should use an integration test or abstract the FileSystem, but
        // given constraints,
        // we can assume if it passes the size check, it hits the repo or IO.

        // Let's just test that it reaches the application check, implying it passed the
        // size check.
        // If I make applicationRepository throw an exception (e.g. not found), I can
        // verify flow control.

        when(applicationRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> fileStorageService.storeDocument("testuser", 1L, "TRANSCRIPT", file));

        // If it was size error, message would be different.
        assertTrue(exception.getMessage().contains("Application not found"));
    }
}

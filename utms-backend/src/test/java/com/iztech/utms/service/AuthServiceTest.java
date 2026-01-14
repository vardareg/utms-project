package com.iztech.utms.service;

import com.iztech.utms.model.PasswordResetToken;
import com.iztech.utms.model.User;
import com.iztech.utms.repository.PasswordResetTokenRepository;
import com.iztech.utms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private NotificationService notificationService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, tokenRepository, passwordEncoder, notificationService);
    }

    @Test
    void initiatePasswordReset_FoundUser_CreatesToken() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        authService.initiatePasswordReset("test@example.com");

        verify(tokenRepository).deleteByUser(user);
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertNotNull(savedToken.getToken());
        assertEquals(user, savedToken.getUser());
        assertTrue(savedToken.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    @Test
    void initiatePasswordReset_UserNotFound_DoesNothing() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        authService.initiatePasswordReset("unknown@example.com");

        verify(tokenRepository, never()).save(any());
    }

    @Test
    void completePasswordReset_ValidToken_UpdatesPassword() {
        User user = new User();
        PasswordResetToken token = new PasswordResetToken(user);
        String tokenStr = token.getToken();

        when(tokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPass")).thenReturn("hashedPass");

        authService.completePasswordReset(tokenStr, "newPass");

        verify(userRepository).save(user);
        verify(tokenRepository).delete(token);
    }

    @Test
    void completePasswordReset_ExpiredToken_ThrowsException() {
        User user = new User();
        PasswordResetToken token = new PasswordResetToken(user);
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1)); // Expired
        String tokenStr = token.getToken();

        when(tokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(token));

        assertThrows(RuntimeException.class, () -> authService.completePasswordReset(tokenStr, "newPass"));

        verify(userRepository, never()).save(any());
    }
}

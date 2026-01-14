package com.iztech.utms.service;

import com.iztech.utms.model.PasswordResetToken;
import com.iztech.utms.model.User;
import com.iztech.utms.repository.PasswordResetTokenRepository;
import com.iztech.utms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    @Transactional
    public void initiatePasswordReset(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        // Security: Always return void/success to prevent email enumeration
        if (userOptional.isEmpty()) {
            System.out.println("Password reset requested for non-existent email: " + email);
            return;
        }

        User user = userOptional.get();
        // Remove existing token if any
        tokenRepository.deleteByUser(user);

        // Create new token
        PasswordResetToken token = new PasswordResetToken(user);
        tokenRepository.save(token);

        // Simulate Email Sending
        String resetLink = "http://localhost:5173/reset-password?token=" + token.getToken();
        notificationService.sendNotification(email, "Password Reset Request",
                "Click the following link to reset your password: " + resetLink);
    }

    @Transactional
    public void completePasswordReset(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid password reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }
}

package com.iztech.utms.service;

import com.iztech.utms.model.User;
import com.iztech.utms.payload.CreateUserRequest;
import com.iztech.utms.payload.UserDto;
import com.iztech.utms.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private com.iztech.utms.repository.PasswordResetTokenRepository tokenRepository;

    @Mock
    private com.iztech.utms.repository.StudentProfileRepository studentProfileRepository;

    @Mock
    private com.iztech.utms.repository.ApplicationRepository applicationRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testCreateUser_Success() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("admin2");
        request.setEmail("admin2@example.com");
        request.setPassword("password");
        request.setRole(User.Role.ROLE_ADMIN);
        request.setUserType("Admin");

        when(userRepository.findByUsername("admin2")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("admin2@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encoded_password");

        User savedUser = User.builder()
                .id(1L)
                .username("admin2")
                .email("admin2@example.com")
                .role(User.Role.ROLE_ADMIN)
                .userType("Admin")
                .enabled(true)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.createUser(request);

        assertNotNull(result);
        assertEquals("admin2", result.getUsername());
        assertTrue(result.isEnabled());
    }

    @Test
    public void testDeleteUser_Success() {
        Long userId = 1L;
        User user = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(studentProfileRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        org.mockito.Mockito.verify(tokenRepository).deleteByUser(user);
        org.mockito.Mockito.verify(studentProfileRepository).deleteById(userId);
        org.mockito.Mockito.verify(applicationRepository).deleteByStudent(user);
        org.mockito.Mockito.verify(userRepository).delete(user);
    }
}

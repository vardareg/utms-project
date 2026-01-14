package com.iztech.utms.service;

import com.iztech.utms.model.User;
import com.iztech.utms.payload.CreateUserRequest;
import com.iztech.utms.payload.UpdateUserRequest;
import com.iztech.utms.payload.UserDto;
import com.iztech.utms.repository.UserRepository;
import com.iztech.utms.repository.PasswordResetTokenRepository;
import com.iztech.utms.repository.StudentProfileRepository;
import com.iztech.utms.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final ApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .userType(request.getUserType())
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Transactional
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        if (request.getUserType() != null) {
            user.setUserType(request.getUserType());
        }

        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Delete Password Reset Tokens
        tokenRepository.deleteByUser(user);

        // 2. Delete Student Profile (if exists)
        if (studentProfileRepository.existsById(id)) {
            studentProfileRepository.deleteById(id);
        }

        // 3. Delete Applications
        applicationRepository.deleteByStudent(user);

        // 4. Delete User
        userRepository.delete(user);
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .userType(user.getUserType())
                .enabled(user.isEnabled())
                .build();
    }
}

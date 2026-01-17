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
    private final com.iztech.utms.repository.AdministrativeProfileRepository administrativeProfileRepository;
    private final com.iztech.utms.repository.DepartmentRepository departmentRepository;
    private final com.iztech.utms.repository.FacultyRepository facultyRepository;

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
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .userType(request.getUserType())
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        // Handle Administrative Profile Creation (Strict Scope Enforcement)
        if (request.getRole() == User.Role.ROLE_DEAN_OFFICE_STAFF) {
            if (request.getFacultyId() == null) {
                throw new RuntimeException("Validation Error: Dean's Office Staff must have a Faculty assigned.");
            }
            if (request.getDepartmentId() != null) {
                throw new RuntimeException(
                        "Validation Error: Dean's Office Staff cannot be restricted to a Department.");
            }
            // Create Profile
            com.iztech.utms.model.AdministrativeProfile profile = new com.iztech.utms.model.AdministrativeProfile();
            profile.setUser(savedUser);
            profile.setFaculty(facultyRepository.findById(request.getFacultyId())
                    .orElseThrow(() -> new RuntimeException("Faculty not found")));
            profile.setDepartment(null);
            administrativeProfileRepository.save(profile);

        } else if (request.getRole() == User.Role.ROLE_YGK) {
            if (request.getDepartmentId() == null) {
                throw new RuntimeException("Validation Error: YGK Member must have a Department assigned.");
            }
            if (request.getFacultyId() != null) {
                throw new RuntimeException(
                        "Validation Error: YGK Member cannot have direct Faculty scope (it is derived).");
            }
            // Create Profile
            com.iztech.utms.model.AdministrativeProfile profile = new com.iztech.utms.model.AdministrativeProfile();
            profile.setUser(savedUser);
            profile.setDepartment(departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found")));
            administrativeProfileRepository.save(profile);

        } else if (request.getRole() == User.Role.ROLE_STUDENT) {
            // Handle TCKN for Students (Optional but recommended for Admin creation)
            if (request.getTckn() != null && !request.getTckn().isEmpty()) {
                if (studentProfileRepository.findByTckn(request.getTckn()).isPresent()) {
                    throw new RuntimeException("Validation Error: TCKN already registered.");
                }

                // Create Student Profile immediately
                com.iztech.utms.model.StudentProfile profile = new com.iztech.utms.model.StudentProfile();
                profile.setUser(savedUser);
                profile.setTckn(request.getTckn());
                studentProfileRepository.save(profile);
            }
        }

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

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        User.Role newRole = request.getRole() != null ? request.getRole() : user.getRole();

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

        // Handle Administrative Profile updates
        var existingProfile = administrativeProfileRepository.findById(id).orElse(null);

        if (newRole == User.Role.ROLE_DEAN_OFFICE_STAFF) {
            // Dean's Office Staff must have faculty assignment
            if (request.getFacultyId() != null) {
                var faculty = facultyRepository.findById(request.getFacultyId())
                        .orElseThrow(() -> new RuntimeException("Faculty not found"));
                if (existingProfile != null) {
                    existingProfile.setFaculty(faculty);
                    existingProfile.setDepartment(null);
                    administrativeProfileRepository.save(existingProfile);
                } else {
                    var profile = new com.iztech.utms.model.AdministrativeProfile();
                    profile.setUser(updatedUser);
                    profile.setFaculty(faculty);
                    profile.setDepartment(null);
                    administrativeProfileRepository.save(profile);
                }
            }
        } else if (newRole == User.Role.ROLE_YGK) {
            // YGK must have department assignment
            if (request.getDepartmentId() != null) {
                var department = departmentRepository.findById(request.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found"));
                if (existingProfile != null) {
                    existingProfile.setDepartment(department);
                    existingProfile.setFaculty(null);
                    administrativeProfileRepository.save(existingProfile);
                } else {
                    var profile = new com.iztech.utms.model.AdministrativeProfile();
                    profile.setUser(updatedUser);
                    profile.setDepartment(department);
                    profile.setFaculty(null);
                    administrativeProfileRepository.save(profile);
                }
            }
        } else {
            // For non-administrative roles (ADMIN, STUDENT, OIDB), clear the assignment
            if (existingProfile != null) {
                administrativeProfileRepository.delete(existingProfile);
            }
        }

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
        Integer facultyId = null;
        String facultyName = null;
        Integer departmentId = null;
        String departmentName = null;

        // Fetch AdministrativeProfile if exists
        var profile = administrativeProfileRepository.findById(user.getId()).orElse(null);
        if (profile != null) {
            if (profile.getFaculty() != null) {
                facultyId = profile.getFaculty().getId();
                facultyName = profile.getFaculty().getName();
            }
            if (profile.getDepartment() != null) {
                departmentId = profile.getDepartment().getId();
                departmentName = profile.getDepartment().getName();
            }
        }

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .userType(user.getUserType())
                .enabled(user.isEnabled())
                .facultyId(facultyId)
                .facultyName(facultyName)
                .departmentId(departmentId)
                .departmentName(departmentName)
                .build();
    }
}

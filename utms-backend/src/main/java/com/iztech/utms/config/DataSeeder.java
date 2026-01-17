package com.iztech.utms.config;

import com.iztech.utms.model.User;
import com.iztech.utms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Profile("dev") // Only run in development mode
public class DataSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (userRepository.count() == 0) {
                // ... (creation logic remains as fallback, but data.sql likely handled it)
                // Create Student
                User student = new User();
                student.setUsername("student");
                student.setEmail("student@iztech.edu.tr");
                student.setPasswordHash(passwordEncoder.encode("password123"));
                student.setRole(User.Role.ROLE_STUDENT);
                student.setUserType("STUDENT");
                userRepository.save(student);

                // Create OIDB User
                User oidb = new User();
                oidb.setUsername("oidb");
                oidb.setEmail("oidb@iztech.edu.tr");
                oidb.setPasswordHash(passwordEncoder.encode("password123"));
                oidb.setRole(User.Role.ROLE_OIDB);
                oidb.setUserType("STAFF");
                userRepository.save(oidb);

                // Create Dean
                User dean = new User();
                dean.setUsername("dean");
                dean.setEmail("dean@iztech.edu.tr");
                dean.setPasswordHash(passwordEncoder.encode("password123"));
                dean.setRole(User.Role.ROLE_DEAN_OFFICE_STAFF);
                dean.setUserType("FACULTY");
                userRepository.save(dean);
            }
        };
    }
}

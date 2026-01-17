package com.iztech.utms.service;

import com.iztech.utms.dto.StudentProfileDto;
import com.iztech.utms.model.StudentProfile;
import com.iztech.utms.model.User;
import com.iztech.utms.repository.StudentProfileRepository;
import com.iztech.utms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

        private final StudentProfileRepository profileRepository;
        private final UserRepository userRepository;

        public StudentProfileDto.Response getProfile(String username) {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                return profileRepository.findById(user.getId())
                                .map(profile -> mapToResponse(profile, user))
                                .orElse(null); // Return null if profile not found (Frontend handles this)
        }

        @Transactional
        public StudentProfileDto.Response upsertProfile(String username, StudentProfileDto.Request request) {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                StudentProfile profile = profileRepository.findById(user.getId())
                                .orElse(StudentProfile.builder()
                                                .user(user)
                                                .build());

                // Update fields (TCKN is not updated as it's set during registration)
                profile.setCurrentUniversity(request.getCurrentUniversity());
                profile.setCurrentProgram(request.getCurrentProgram());
                profile.setOverallGpa(request.getOverallGpa());
                profile.setHasDisciplinaryRecord(request.isHasDisciplinaryRecord());

                StudentProfile saved = profileRepository.save(profile);
                return mapToResponse(saved, user);
        }

        private StudentProfileDto.Response mapToResponse(StudentProfile profile, User user) {
                return StudentProfileDto.Response.builder()
                                .tckn(profile.getTckn())
                                .currentUniversity(profile.getCurrentUniversity())
                                .currentProgram(profile.getCurrentProgram())
                                .overallGpa(profile.getOverallGpa())
                                .hasDisciplinaryRecord(profile.isHasDisciplinaryRecord())
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .build();
        }
}

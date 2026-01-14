package com.iztech.utms.controller;

import com.iztech.utms.dto.StudentProfileDto;
import com.iztech.utms.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<StudentProfileDto.Response> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("DEBUG: GET /api/student/profile called by " + userDetails.getUsername() + ", Authorities: "
                + userDetails.getAuthorities());
        StudentProfileDto.Response profile = studentService.getProfile(userDetails.getUsername());
        if (profile == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(profile);
    }

    @PostMapping
    public ResponseEntity<StudentProfileDto.Response> upsertProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody StudentProfileDto.Request request) {
        return ResponseEntity.ok(studentService.upsertProfile(userDetails.getUsername(), request));
    }
}

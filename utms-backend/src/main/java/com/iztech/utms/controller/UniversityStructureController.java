package com.iztech.utms.controller;

import com.iztech.utms.model.UniversityStructure.Department;
import com.iztech.utms.model.UniversityStructure.Faculty;
import com.iztech.utms.repository.DepartmentRepository;
import com.iztech.utms.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/structure")
@RequiredArgsConstructor
public class UniversityStructureController {

    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;

    @GetMapping("/faculties")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Faculty>> getAllFaculties() {
        return ResponseEntity.ok(facultyRepository.findAll());
    }

    @GetMapping("/departments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(departmentRepository.findAll());
    }
}

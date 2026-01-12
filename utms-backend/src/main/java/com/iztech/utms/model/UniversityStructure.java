package com.iztech.utms.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Contains Faculty and Department entities mapping to 'faculties' and 'departments'.
 * Grouped in one file for organizational clarity.
 */
public class UniversityStructure {

    @Entity
    @Table(name = "faculties")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Faculty {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(nullable = false, unique = true, length = 100)
        private String name;
    }

    @Entity
    @Table(name = "departments")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Department {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "faculty_id", nullable = false)
        private Faculty faculty;

        @Column(nullable = false, length = 100)
        private String name;

        // PR-08: Quota Rule
        @Column(nullable = false)
        private Integer quota;
    }
}
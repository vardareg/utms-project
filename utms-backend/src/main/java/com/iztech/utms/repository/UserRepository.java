package com.iztech.utms.repository;

import com.iztech.utms.model.User;
import com.iztech.utms.model.UniversityStructure.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// 1. User Repository
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Required for Authentication (UC-SYS-01)
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
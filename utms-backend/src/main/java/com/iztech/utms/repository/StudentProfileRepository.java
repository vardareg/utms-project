package main.java.com.iztech.utms.repository;

import com.iztech.utms.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    // Finds the profile by the User ID (One-to-One mapping)
    Optional<StudentProfile> findByUserId(Long userId);
}
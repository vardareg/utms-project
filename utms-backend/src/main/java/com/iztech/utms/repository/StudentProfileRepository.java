package com.iztech.utms.repository;

import com.iztech.utms.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    java.util.Optional<StudentProfile> findByTckn(String tckn);
}
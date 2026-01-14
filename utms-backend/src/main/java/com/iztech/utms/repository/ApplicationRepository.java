package com.iztech.utms.repository;

import com.iztech.utms.model.Application;
import com.iztech.utms.model.Application.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByStudentId(Long studentId);

    List<Application> findByStatus(ApplicationStatus status);

    List<Application> findByTargetDepartmentIdAndStatus(Integer departmentId, ApplicationStatus status);

    boolean existsByStudentIdAndTargetDepartmentId(Long studentId, Integer targetDepartmentId);

    java.util.Optional<Application> findByStudentIdAndTargetDepartmentId(Long studentId, Integer targetDepartmentId);

    // WP-5 ADDITION: Fetch all applications for a department, sorted by Composite
    // Score (PR-07/PR-09)
    // Used for generating the Ranking List
    // Sort Order: Composite Score (Desc) -> YKS Score (Desc) -> GPA (Desc) ->
    // Submission Date (Asc)
    List<Application> findByTargetDepartmentIdOrderByCompositeScoreDescYksScoreDescConvertedGpaDescSubmissionDateAsc(
            Integer departmentId);

    void deleteByStudent(com.iztech.utms.model.User student);
}
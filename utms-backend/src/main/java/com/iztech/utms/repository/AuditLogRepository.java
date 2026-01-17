package com.iztech.utms.repository;

import com.iztech.utms.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByTargetApplicationId(Long targetApplicationId);

    List<AuditLog> findByActorUsername(String actorUsername);

    List<AuditLog> findByActionTypeIn(java.util.Collection<com.iztech.utms.model.ActionType> types,
            org.springframework.data.domain.Sort sort);

    @org.springframework.data.jpa.repository.Query("SELECT log FROM AuditLog log, Application app " +
            "WHERE log.targetApplicationId = app.id " +
            "AND app.targetDepartment.id = :departmentId " +
            "AND log.actionType IN :actions")
    List<AuditLog> findLogsByDepartment(Integer departmentId,
            java.util.Collection<com.iztech.utms.model.ActionType> actions,
            org.springframework.data.domain.Sort sort);

    @org.springframework.data.jpa.repository.Query("SELECT log FROM AuditLog log, Application app " +
            "WHERE log.targetApplicationId = app.id " +
            "AND app.targetDepartment.faculty.id = :facultyId " +
            "AND log.actionType IN :actions")
    List<AuditLog> findLogsByFaculty(Integer facultyId,
            java.util.Collection<com.iztech.utms.model.ActionType> actions,
            org.springframework.data.domain.Sort sort);
}

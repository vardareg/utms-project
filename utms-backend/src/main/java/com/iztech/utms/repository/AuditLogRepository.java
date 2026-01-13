package com.iztech.utms.repository;

import com.iztech.utms.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByTargetApplicationId(Long targetApplicationId);

    List<AuditLog> findByActorUsername(String actorUsername);
}

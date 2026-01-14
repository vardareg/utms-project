package com.iztech.utms.service;

import com.iztech.utms.model.ActionType;
import com.iztech.utms.model.Application;
import com.iztech.utms.model.Application.ApplicationStatus;
import com.iztech.utms.model.AuditLog;
import com.iztech.utms.repository.ApplicationRepository;
import com.iztech.utms.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchivalService {

    private final ApplicationRepository applicationRepository;
    private final AuditLogRepository auditLogRepository;

    @Scheduled(cron = "0 0 2 * * ?") // 2:00 AM every day
    @Transactional
    public void archiveOldData() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);

        log.info("Starting archival process for applications older than {}", cutoffDate);

        List<Application> applicationsToArchive = applicationRepository.findBySubmissionDateBeforeAndStatusNot(
                cutoffDate, ApplicationStatus.ARCHIVED);

        if (applicationsToArchive.isEmpty()) {
            log.info("No applications found to archive.");
            return;
        }

        for (Application app : applicationsToArchive) {
            app.setStatus(ApplicationStatus.ARCHIVED);

            AuditLog logEntry = AuditLog.builder()
                    .actorUsername("SYSTEM")
                    .actionType(ActionType.ARCHIVE)
                    .targetApplicationId(app.getId())
                    .details("Auto-archived due to age > 90 days")
                    .build();

            auditLogRepository.save(logEntry);
        }

        applicationRepository.saveAll(applicationsToArchive);

        log.info("Archived {} applications.", applicationsToArchive.size());
    }
}

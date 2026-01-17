package com.iztech.utms.controller;

import com.iztech.utms.dto.AnnouncementDto;
import com.iztech.utms.model.Announcement;
import com.iztech.utms.model.Application;
import com.iztech.utms.model.Application.ApplicationStatus;
import com.iztech.utms.model.UniversityStructure.Department;
import com.iztech.utms.repository.AnnouncementRepository;
import com.iztech.utms.repository.ApplicationRepository;
import com.iztech.utms.repository.DepartmentRepository;
import com.iztech.utms.service.EvaluationService;
import com.iztech.utms.service.EvaluationService.RankingDTO;
import com.iztech.utms.service.EvaluationService.RankingResponse;
import com.iztech.utms.service.ExportService;
import com.iztech.utms.service.FileStorageService;
import com.iztech.utms.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/announcements")
public class ResultController {

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    @Qualifier("pdfExportService")
    private ExportService exportService;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/publish-results/{departmentId}")
    @PreAuthorize("hasRole('OIDB')")
    @Transactional
    public ResponseEntity<AnnouncementDto> publishResults(@PathVariable Integer departmentId) {
        // 1. Fetch Finalized Ranking
        RankingResponse ranking = evaluationService.generateRanking(departmentId);
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        // 2. Update Application Statuses
        // Primary List -> APPROVED
        updateStatus(ranking.getPrimaryList(), ApplicationStatus.APPROVED);

        // Wait List -> WAITLIST
        updateStatus(ranking.getWaitList(), ApplicationStatus.WAITLIST);

        // Others (Eligible but not ranked) -> REJECTED (Quota full)
        // We need to fetch all candidates again or identify who was left out.
        // Logic: Fetch all applications for dept. If status is UNDER_REVIEW, set to
        // REJECTED.
        // Those who were APPROVED/WAITLIST just got updated above, so we can verify.
        // However, updateStatus saves them.
        // So anyone STILL in UNDER_REVIEW after the above updates is REJECTED.
        List<Application> pendingApps = applicationRepository.findByTargetDepartmentIdAndStatus(departmentId,
                ApplicationStatus.UNDER_REVIEW);
        for (Application app : pendingApps) {
            // Double check they are not in primary/waitlist (though status check handles it
            // mostly)
            boolean isRanked = isApplicationRanked(app.getId(), ranking);
            if (!isRanked) {
                app.setStatus(ApplicationStatus.REJECTED);
                applicationRepository.save(app);
            }
        }

        // 3. Generate PDF
        ByteArrayInputStream pdfStream = exportService.export(ranking);
        String fileName = "Result_Announcement_" + department.getName().replaceAll("\\s+", "_") + ".pdf";

        // 4. Create Announcement with File
        String filePath = fileStorageService.storeGenericFile(fileName, pdfStream, "RESULT_");

        Announcement announcement = new Announcement();
        announcement.setTitle("Placement Results: " + department.getName());
        announcement.setContent("The placement results for " + department.getName()
                + " have been announced. Please find the list attached.");
        announcement.setPriority(Announcement.Priority.CRITICAL); // High priority
        announcement.setRelatedDepartmentId(departmentId);
        announcement.setResultAnnouncement(true);
        announcement.setAttachmentName(fileName);
        announcement.setAttachmentPath(filePath);

        Announcement saved = announcementRepository.save(announcement);

        // 5. Send Notifications
        List<Application> allApps = applicationRepository
                .findByTargetDepartmentIdOrderByCompositeScoreDescYksScoreDescConvertedGpaDescSubmissionDateAsc(
                        departmentId);
        for (Application app : allApps) {
            notificationService.sendResultNotification(
                    app.getStudent().getEmail(), // Assuming email is on User
                    app.getStudent().getUsername(), // Or name if available
                    app.getStatus().name(),
                    department.getName());
        }

        return ResponseEntity.ok(convertToDto(saved));
    }

    private void updateStatus(List<RankingDTO> list, ApplicationStatus status) {
        if (list == null)
            return;
        for (RankingDTO dto : list) {
            Optional<Application> appOpt = applicationRepository.findById(dto.getTrackingId()); // Using ID as
                                                                                                // trackingId
            if (appOpt.isPresent()) {
                Application app = appOpt.get();
                // only update if not already final (e.g. dont overwrite if already registered,
                // but here we assume first publish)
                app.setStatus(status);
                applicationRepository.save(app);
            }
        }
    }

    private boolean isApplicationRanked(Long appId, RankingResponse ranking) {
        if (ranking.getPrimaryList() != null) {
            if (ranking.getPrimaryList().stream().anyMatch(d -> d.getTrackingId().equals(appId)))
                return true;
        }
        if (ranking.getWaitList() != null) {
            if (ranking.getWaitList().stream().anyMatch(d -> d.getTrackingId().equals(appId)))
                return true;
        }
        return false;
    }

    private AnnouncementDto convertToDto(Announcement announcement) {
        AnnouncementDto dto = new AnnouncementDto(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getContent(),
                announcement.getPublishDate(),
                announcement.getPriority(),
                announcement.isActive());

        dto.setAttachmentName(announcement.getAttachmentName());
        if (announcement.getAttachmentPath() != null) {
            dto.setDownloadUrl("/public/announcements/" + announcement.getId() + "/attachment");
        }
        return dto;
    }
}

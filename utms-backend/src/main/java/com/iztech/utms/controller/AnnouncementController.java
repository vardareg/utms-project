package com.iztech.utms.controller;

import com.iztech.utms.dto.AnnouncementDto;
import com.iztech.utms.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private com.iztech.utms.service.FileStorageService fileStorageService;

    @GetMapping("/public/announcements")
    public ResponseEntity<List<AnnouncementDto>> getAllPublicAnnouncements() {
        return ResponseEntity.ok(announcementService.getAllPublicAnnouncements());
    }

    @CrossOrigin(origins = "http://localhost:5173") // Allow frontend access if needed for clean URL
    @GetMapping("/public/announcements/{id}/attachment")
    public ResponseEntity<org.springframework.core.io.Resource> getAttachment(@PathVariable Long id) {
        String path = announcementService.getAttachmentPath(id);
        if (path == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            org.springframework.core.io.Resource resource = fileStorageService.loadFileAsResource(path);
            String contentType = "application/pdf"; // Enforced in FileStorageService

            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/oidb/announcements")
    @PreAuthorize("hasAnyRole('OIDB', 'ADMIN')")
    public ResponseEntity<AnnouncementDto> createAnnouncement(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("priority") String priority,
            @RequestParam(value = "file", required = false) org.springframework.web.multipart.MultipartFile file) {

        AnnouncementDto dto = new AnnouncementDto();
        dto.setTitle(title);
        dto.setContent(content);
        dto.setPriority(com.iztech.utms.model.Announcement.Priority.valueOf(priority));

        return ResponseEntity.ok(announcementService.createAnnouncement(dto, file));
    }

    @DeleteMapping("/oidb/announcements/{id}")
    @PreAuthorize("hasAnyRole('OIDB', 'ADMIN')")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok().build();
    }
}

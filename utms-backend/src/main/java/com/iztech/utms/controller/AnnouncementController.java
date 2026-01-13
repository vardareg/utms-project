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

    @GetMapping("/public/announcements")
    public ResponseEntity<List<AnnouncementDto>> getAllPublicAnnouncements() {
        return ResponseEntity.ok(announcementService.getAllPublicAnnouncements());
    }

    @PostMapping("/oidb/announcements")
    @PreAuthorize("hasRole('OIDB')")
    public ResponseEntity<AnnouncementDto> createAnnouncement(@RequestBody AnnouncementDto dto) {
        return ResponseEntity.ok(announcementService.createAnnouncement(dto));
    }

    @DeleteMapping("/oidb/announcements/{id}")
    @PreAuthorize("hasRole('OIDB')")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok().build();
    }
}

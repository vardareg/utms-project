package com.iztech.utms.service;

import com.iztech.utms.dto.AnnouncementDto;
import com.iztech.utms.model.Announcement;
import com.iztech.utms.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public AnnouncementDto createAnnouncement(AnnouncementDto dto,
            org.springframework.web.multipart.MultipartFile file) {
        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setPriority(dto.getPriority());
        // active and publishDate are set by default in constructor, but can be
        // overridden if needed
        if (dto.getPublishDate() != null) {
            announcement.setPublishDate(dto.getPublishDate());
        }

        if (file != null && !file.isEmpty()) {
            String path = fileStorageService.storeGenericFile(file, "ANNOUNCEMENT_");
            announcement.setAttachmentPath(path);
            announcement.setAttachmentName(file.getOriginalFilename());
        }

        Announcement saved = announcementRepository.save(announcement);
        return convertToDto(saved);
    }

    public List<AnnouncementDto> getAllPublicAnnouncements() {
        List<Announcement> announcements = announcementRepository.findByActiveTrueOrderByPublishDateDesc();
        return announcements.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void deleteAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
        announcement.setActive(false);
        announcementRepository.save(announcement);
    }

    private AnnouncementDto convertToDto(Announcement announcement) {
        AnnouncementDto dto = new AnnouncementDto(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getContent(),
                announcement.getPublishDate(),
                announcement.getPriority(),
                announcement.isActive());

        dto.setResultAnnouncement(announcement.isResultAnnouncement());
        dto.setRelatedDepartmentId(announcement.getRelatedDepartmentId());

        dto.setAttachmentName(announcement.getAttachmentName());
        if (announcement.getAttachmentPath() != null) {
            dto.setDownloadUrl("/public/announcements/" + announcement.getId() + "/attachment");
        }

        return dto;
    }

    public String getAttachmentPath(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
        return announcement.getAttachmentPath();
    }

}

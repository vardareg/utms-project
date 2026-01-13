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

    public AnnouncementDto createAnnouncement(AnnouncementDto dto) {
        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setPriority(dto.getPriority());
        // active and publishDate are set by default in constructor, but can be
        // overridden if needed
        if (dto.getPublishDate() != null) {
            announcement.setPublishDate(dto.getPublishDate());
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
        return new AnnouncementDto(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getContent(),
                announcement.getPublishDate(),
                announcement.getPriority(),
                announcement.isActive());
    }
}

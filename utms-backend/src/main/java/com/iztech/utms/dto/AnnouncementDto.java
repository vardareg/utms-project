package com.iztech.utms.dto;

import com.iztech.utms.model.Announcement;
import java.time.LocalDateTime;

public class AnnouncementDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime publishDate;
    private Announcement.Priority priority;
    private boolean active;
    private String attachmentName;
    private String downloadUrl;

    public AnnouncementDto() {
    }

    public AnnouncementDto(Long id, String title, String content, LocalDateTime publishDate,
            Announcement.Priority priority, boolean active, String attachmentName, String downloadUrl) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.publishDate = publishDate;
        this.priority = priority;
        this.active = active;
        this.attachmentName = attachmentName;
        this.downloadUrl = downloadUrl;
    }

    // Legacy constructor for backward compatibility if needed, or just update
    // callers
    public AnnouncementDto(Long id, String title, String content, LocalDateTime publishDate,
            Announcement.Priority priority, boolean active) {
        this(id, title, content, publishDate, priority, active, null, null);
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public Announcement.Priority getPriority() {
        return priority;
    }

    public void setPriority(Announcement.Priority priority) {
        this.priority = priority;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}

package com.iztech.utms.service;

public interface NotificationService {
    void sendNotification(String to, String subject, String body);

    void sendResultNotification(String to, String studentName, String status, String deptName);
}

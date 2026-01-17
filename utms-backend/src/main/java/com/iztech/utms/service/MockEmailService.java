package com.iztech.utms.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;
import java.time.LocalDateTime;

@Service
@Primary
public class MockEmailService implements NotificationService {

    @Override
    @Async
    public void sendNotification(String to, String subject, String body) {
        // Simulate network delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logEmail(to, subject, body);
    }

    @Override
    @Async
    public void sendResultNotification(String to, String studentName, String status, String deptName) {
        String subject = "UTMS Application Result - " + deptName;
        String body;

        switch (status) {
            case "APPROVED":
                body = "Dear " + studentName + ",\n\n" +
                        "Congratulations! Your application to " + deptName + " has been APPROVED.\n" +
                        "Please check your dashboard for the official acceptance letter and registration details.\n\n" +
                        "Best regards,\nStudent Affairs";
                break;
            case "WAITLIST":
                body = "Dear " + studentName + ",\n\n" +
                        "You have been placed on the WAITLIST for " + deptName + ".\n" +
                        "We will notify you if a spot becomes available.\n\n" +
                        "Best regards,\nStudent Affairs";
                break;
            case "REJECTED":
                body = "Dear " + studentName + ",\n\n" +
                        "We regret to inform you that your application to " + deptName + " was NOT successful.\n" +
                        "We wish you the best in your future endeavors.\n\n" +
                        "Best regards,\nStudent Affairs";
                break;
            default:
                body = "Dear " + studentName + ",\n\n" +
                        "Your application status for " + deptName + " has been updated to: " + status + ".\n" +
                        "Please check your dashboard for details.\n\n" +
                        "Best regards,\nStudent Affairs";
        }

        sendNotification(to, subject, body);
    }

    private void logEmail(String to, String subject, String body) {
        String log = String.format(
                "\n=================[ EMAIL SENT ]=================\n" +
                        "Time:    %s\n" +
                        "To:      %s\n" +
                        "Subject: %s\n" +
                        "Body:    %s\n" +
                        "================================================\n",
                LocalDateTime.now(), to, subject, body);

        System.out.println(log);
    }
}

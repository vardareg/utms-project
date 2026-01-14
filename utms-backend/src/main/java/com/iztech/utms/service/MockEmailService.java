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

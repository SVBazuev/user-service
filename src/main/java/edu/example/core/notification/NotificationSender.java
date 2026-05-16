package edu.example.core.notification;

import edu.example.core.dto.NotificationOperation;

public interface NotificationSender {
    void sendNotification(NotificationOperation operation, String email);
}

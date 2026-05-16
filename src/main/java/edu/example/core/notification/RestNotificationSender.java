package edu.example.core.notification;

import org.springframework.stereotype.Component;

import edu.example.core.dto.NotificationOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RestNotificationSender implements NotificationSender {

    @Override
    public void sendNotification(NotificationOperation operation, String email) {
        log.info("REST notification stub: {} {}", operation, email);
    }
}

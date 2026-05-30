package edu.example.core.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import edu.example.core.dto.NotificationOperation;

@Slf4j
@Component
@RequiredArgsConstructor
public class DelegatingNotificationSender implements NotificationSender {
    private final KafkaNotificationSender kafkaSender;
    private final RestNotificationSender restSender;

    @Override
    public void sendNotification(NotificationOperation operation, String email) {
        try {
            kafkaSender.sendNotification(operation, email);
        } catch (Exception e) {
            log.warn("Kafka failed, falling back to REST", e);
            restSender.sendNotification(operation, email);
        }
    }
}

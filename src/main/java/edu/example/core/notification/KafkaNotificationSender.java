package edu.example.core.notification;

import edu.example.core.dto.NotificationOperation;
import edu.example.core.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaNotificationSender implements NotificationSender {

    private final KafkaTemplate<String, NotificationRequest> kafkaTemplate;
    
    @Value("${app.kafka.topic.user-events}")
    private String topic;

    @Override
    public void sendNotification(NotificationOperation operation, String email) {

        NotificationRequest request = new NotificationRequest(operation, email);
        try {
            var result = kafkaTemplate.send(topic, request).get(5, TimeUnit.SECONDS);
            log.info("Sent via Kafka, offset: {}", result.getRecordMetadata().offset());
        } catch (Exception e) {
            log.error("Kafka send failed", e);
            throw new RuntimeException("Kafka send failed", e);
        }
    }
}

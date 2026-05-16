package edu.example.core.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import edu.example.core.dto.NotificationOperation;
import edu.example.core.dto.NotificationRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestNotificationSender implements NotificationSender {
    private final WebClient.Builder webClientBuilder;
    @Value("${app.notification-service.url}")
    private String notificationUrl;

    @Override
    public void sendNotification(NotificationOperation operation, String email) {
        NotificationRequest request = new NotificationRequest(operation, email);
        webClientBuilder.build()
            .post()
            .uri(notificationUrl + "/email")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Void.class)
            .doOnSuccess(v -> log.info("REST notification sent"))
            .doOnError(e -> log.error("REST notification failed", e))
            .block();
    }
}

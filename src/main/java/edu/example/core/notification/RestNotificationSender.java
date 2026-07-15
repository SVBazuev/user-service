package edu.example.core.notification;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import edu.example.core.dto.NotificationOperation;
import edu.example.core.dto.NotificationRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestNotificationSender implements NotificationSender {

    private final WebClient.Builder webClientBuilder;

    @CircuitBreaker(
        name = "notificationService",
        fallbackMethod = "fallbackSendNotification")
    @Override
    public void sendNotification(NotificationOperation operation, String email) {
        NotificationRequest request = new NotificationRequest(operation, email);
        webClientBuilder.build()
            .post()
            .uri("http://notification-service/api/notifications/email")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Void.class)
            .doOnSuccess(v -> log.info("REST notification sent via load-balanced client"))
            .doOnError(e -> log.error("REST notification failed", e))
            .block();
    }

    @SuppressWarnings("unused")
    private void fallbackSendNotification(NotificationOperation operation, String email, Throwable t) {
        log.warn("Fallback: Could not send notification to {} due to: {}", email, t.getMessage());
    }
}

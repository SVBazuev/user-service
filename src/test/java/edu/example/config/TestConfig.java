package edu.example.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import edu.example.core.dto.NotificationRequest;
import edu.example.core.notification.NotificationSender;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    public KafkaTemplate<String, NotificationRequest> kafkaTemplate() {
        return Mockito.mock(KafkaTemplate.class);
    }

    @Bean
    @Primary
    public WebClient.Builder webClientBuilder() {
        return Mockito.mock(WebClient.Builder.class);
    }

    @Bean
    @Primary
    public NotificationSender notificationSender() {
        return Mockito.mock(NotificationSender.class);
    }
}

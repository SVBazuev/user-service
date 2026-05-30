package edu.example.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;

import edu.example.core.dto.NotificationRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@TestConfiguration
public class FallbackTestConfig {
    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    public KafkaTemplate<String, NotificationRequest> failingKafkaTemplate() {
        KafkaTemplate<String, NotificationRequest> mock = Mockito.mock(KafkaTemplate.class);
        when(mock.send(anyString(), any(NotificationRequest.class)))
            .thenThrow(new RuntimeException("Kafka down"));
        return mock;
    }
}

package edu.example.notification;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import edu.example.config.FallbackTestConfig;
import edu.example.core.dto.UserRequest;
import edu.example.core.notification.RestNotificationSender;
import edu.example.core.service.UserService;
import edu.example.repository.UserRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = {
    "spring.kafka.bootstrap-servers=localhost:9999"
})
@ActiveProfiles("test")
@Import(FallbackTestConfig.class)
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=" +
        "org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
})
class FallbackNotificationIntegrationTest {

    @SpyBean
    private RestNotificationSender restNotificationSender;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldFallbackToRestWhenKafkaIsDown() throws InterruptedException {
        UserRequest request = new UserRequest(
            "FallbackUser", "fallback@test.ya", 28, "pass123"
        );
        userService.create(request);

        Thread.sleep(500);

        verify(restNotificationSender).sendNotification(any(), any());
    }
}

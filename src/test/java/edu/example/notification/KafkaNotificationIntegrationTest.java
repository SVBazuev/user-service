package edu.example.notification;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import edu.example.core.dto.NotificationOperation;
import edu.example.core.dto.NotificationRequest;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;
import edu.example.core.service.UserService;
import edu.example.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(topics = {"user-events"}, partitions = 1)
@ActiveProfiles("test")
class KafkaNotificationIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private KafkaMessageListenerContainer<String, NotificationRequest> container;
    private BlockingQueue<ConsumerRecord<String, NotificationRequest>> records;

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
            "test-group", "true", embeddedKafkaBroker
        );
        consumerProps.put(
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class
        );
        consumerProps.put(
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            JsonDeserializer.class
        );
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        DefaultKafkaConsumerFactory<String, NotificationRequest> cf = new DefaultKafkaConsumerFactory<>(
            consumerProps,
            new StringDeserializer(),
            new JsonDeserializer<>(NotificationRequest.class, false)
        );
        ContainerProperties containerProps = new ContainerProperties("user-events");
        container = new KafkaMessageListenerContainer<>(cf, containerProps);
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener(
            (MessageListener<String, NotificationRequest>) records::add
        );
        container.start();
        ContainerTestUtils.waitForAssignment(container, 1);
    }

    @AfterEach
    void tearDown() {
        if (container != null) container.stop();
        userRepository.deleteAll();
    }

    @Test
    void shouldSendKafkaMessageOnUserCreate() throws Exception {
        UserRequest request = new UserRequest(
            "KafkaUser", "kafka@test.ya", 25
        );
        userService.create(request);

        ConsumerRecord<String, NotificationRequest> record = records.poll(
            10, TimeUnit.SECONDS
        );
        assertThat(record)
            .isNotNull();
        assertThat(record.value().operation())
            .isEqualTo(NotificationOperation.CREATE);
        assertThat(record.value().email())
            .isEqualTo("kafka@test.ya");
    }

    @Test
    void shouldSendKafkaMessageOnUserDelete() throws Exception {
        UserRequest createRequest = new UserRequest(
            "DeleteKafka", "delete@test.ya", 30
        );
        UserResponse created = userService.create(createRequest);
        while (records.poll(100, TimeUnit.MILLISECONDS) != null) {
            // продолжаем пока не опустеет
        }
        userService.delete(created.id());

        ConsumerRecord<String, NotificationRequest> record = records.poll(
            10, TimeUnit.SECONDS
        );
        assertThat(record)
            .isNotNull();
        assertThat(record.value().operation())
            .isEqualTo(NotificationOperation.DELETE);
        assertThat(record.value().email())
            .isEqualTo("delete@test.ya");
    }
}

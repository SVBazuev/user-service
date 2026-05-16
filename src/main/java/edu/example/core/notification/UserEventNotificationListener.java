package edu.example.core.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import edu.example.core.dto.NotificationOperation;
import edu.example.core.event.UserCreatedEvent;
import edu.example.core.event.UserDeletedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventNotificationListener {

    private final DelegatingNotificationSender notificationSender;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserCreated(UserCreatedEvent event) {
        log.debug("Handling UserCreatedEvent after commit for email: {}", event.email());
        notificationSender.sendNotification(NotificationOperation.CREATE, event.email());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserDeleted(UserDeletedEvent event) {
        log.debug("Handling UserDeletedEvent after commit for email: {}", event.email());
        notificationSender.sendNotification(NotificationOperation.DELETE, event.email());
    }
}

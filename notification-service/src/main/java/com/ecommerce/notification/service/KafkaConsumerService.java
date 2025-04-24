package com.ecommerce.notification.service;

import com.ecommerce.notification.event.OrderEvent;
import com.ecommerce.notification.entity.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final NotificationRepository notificationRepository;

    public KafkaConsumerService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @KafkaListener(topics = "order-topic", groupId = "notification-group",
            containerFactory = "orderEventKafkaListenerContainerFactory")
    public void consumeOrderEvent(OrderEvent orderEvent) {
        logger.info("Received OrderEvent: {}", orderEvent);
        try {
            if ("CREATED".equalsIgnoreCase(orderEvent.getAction())) {
                // Simulate sending a notification (e.g., email)
                String message = String.format("Order Confirmation: Order #%d placed successfully. Total: $%.2f",
                        orderEvent.getOrderId(), orderEvent.getTotalPrice());
                logger.info("Sending notification: {}", message);

                // Save the notification to the database
                Notification notification = new Notification();
                notification.setOrderId(orderEvent.getOrderId());
                notification.setUserId(orderEvent.getUserId());
                notification.setMessage(message);
                notification.setStatus("SENT");
                notificationRepository.save(notification);

                logger.info("Saved notification for order: {}", orderEvent.getOrderId());
            }
        } catch (Exception e) {
            logger.error("Error processing OrderEvent: {}", orderEvent, e);
            // In a production system, consider sending to a dead-letter queue
            throw e;
        }
    }
}
package com.ecommerce.notification.integration;

import com.ecommerce.notification.dto.NotificationResponse;
import com.ecommerce.notification.event.OrderEvent;
import com.ecommerce.notification.entity.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9093", "port=9093" })
@DirtiesContext
class NotificationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void givenOrderEvent_whenPublishedToKafka_thenNotificationIsSaved() {
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderId(1L);
        orderEvent.setUserId(1L);
        orderEvent.setProductId(1L);
        orderEvent.setQuantity(2);
        orderEvent.setTotalPrice(1999.98);
        orderEvent.setStatus("PLACED");
        orderEvent.setAction("CREATED");

        kafkaTemplate.send("order-topic", String.valueOf(orderEvent.getOrderId()), orderEvent);

        await().atMost(5, TimeUnit.SECONDS).until(() -> !notificationRepository.findAll().isEmpty());

        Notification savedNotification = notificationRepository.findAll().get(0);
        assertEquals(1L, savedNotification.getOrderId());
        assertTrue(savedNotification.getMessage().contains("Order Confirmation"));
        assertEquals("SENT", savedNotification.getStatus());
    }

    @Test
    void givenNotificationExists_whenGetById_thenReturnsNotification() {
        Notification notification = new Notification();
        notification.setOrderId(1L);
        notification.setUserId(1L);
        notification.setMessage("Order Confirmation");
        notification.setStatus("SENT");
        Notification savedNotification = notificationRepository.save(notification);

        ResponseEntity<NotificationResponse> response = restTemplate.getForEntity(
                "/api/notifications/" + savedNotification.getId(), NotificationResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Order Confirmation", response.getBody().getMessage());
    }
}
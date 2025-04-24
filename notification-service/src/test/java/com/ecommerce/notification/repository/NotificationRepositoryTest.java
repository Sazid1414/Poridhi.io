package com.ecommerce.notification.repository;

import com.ecommerce.notification.entity.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void saveNotification_ReturnsSavedNotification() {
        Notification notification = new Notification();
        notification.setOrderId(1L);
        notification.setUserId(1L);
        notification.setMessage("Order Confirmation");
        notification.setStatus("SENT");

        Notification savedNotification = notificationRepository.save(notification);

        assertNotNull(savedNotification.getId());
        assertEquals("Order Confirmation", savedNotification.getMessage());
    }

    @Test
    void findById_ReturnsNotification() {
        Notification notification = new Notification();
        notification.setOrderId(1L);
        notification.setUserId(1L);
        notification.setMessage("Order Confirmation");
        notification.setStatus("SENT");
        notificationRepository.save(notification);

        Optional<Notification> foundNotification = notificationRepository.findById(notification.getId());

        assertTrue(foundNotification.isPresent());
        assertEquals("Order Confirmation", foundNotification.get().getMessage());
    }
}
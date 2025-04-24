package com.ecommerce.notification.service;

import com.ecommerce.notification.dto.NotificationRequest;
import com.ecommerce.notification.dto.NotificationResponse;
import com.ecommerce.notification.exception.NotificationNotFoundException;
import com.ecommerce.notification.entity.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import com.ecommerce.notification.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification notification;
    private NotificationRequest notificationRequest;

    @BeforeEach
    void setUp() {
        notification = new Notification();
        notification.setId(1L);
        notification.setOrderId(1L);
        notification.setUserId(1L);
        notification.setMessage("Order Confirmation");
        notification.setStatus("SENT");

        notificationRequest = new NotificationRequest();
        notificationRequest.setOrderId(1L);
        notificationRequest.setUserId(1L);
        notificationRequest.setMessage("Order Confirmation");
        notificationRequest.setStatus("SENT");
    }

    @Test
    void createNotification_ReturnsSavedNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        NotificationResponse response = notificationService.createNotification(notificationRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Order Confirmation", response.getMessage());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void getAllNotifications_ReturnsNotificationList() {
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationRepository.findAll()).thenReturn(notifications);

        List<NotificationResponse> responses = notificationService.getAllNotifications();

        assertEquals(1, responses.size());
        assertEquals("Order Confirmation", responses.get(0).getMessage());
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    void getNotificationById_ReturnsNotification() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        NotificationResponse response = notificationService.getNotificationById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Order Confirmation", response.getMessage());
        verify(notificationRepository, times(1)).findById(1L);
    }

    @Test
    void getNotificationById_NotFound_ThrowsException() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () -> notificationService.getNotificationById(999L));
        verify(notificationRepository, times(1)).findById(999L);
    }

    @Test
    void updateNotification_ReturnsUpdatedNotification() {
        NotificationRequest updateRequest = new NotificationRequest();
        updateRequest.setOrderId(1L);
        updateRequest.setUserId(1L);
        updateRequest.setMessage("Updated Confirmation");
        updateRequest.setStatus("SENT");

        Notification updatedNotification = new Notification();
        updatedNotification.setId(1L);
        updatedNotification.setOrderId(1L);
        updatedNotification.setUserId(1L);
        updatedNotification.setMessage("Updated Confirmation");
        updatedNotification.setStatus("SENT");

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(updatedNotification);

        NotificationResponse response = notificationService.updateNotification(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Updated Confirmation", response.getMessage());
        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void deleteNotification_Success() {
        when(notificationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(notificationRepository).deleteById(1L);

        notificationService.deleteNotification(1L);

        verify(notificationRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteNotification_NotFound_ThrowsException() {
        when(notificationRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotificationNotFoundException.class, () -> notificationService.deleteNotification(999L));
        verify(notificationRepository, times(1)).existsById(999L);
    }
}
package com.ecommerce.notification.service;

import com.ecommerce.notification.dto.NotificationRequest;
import com.ecommerce.notification.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {

    NotificationResponse createNotification(NotificationRequest notificationRequest);
    List<NotificationResponse> getAllNotifications();
    NotificationResponse getNotificationById(Long id);
    NotificationResponse updateNotification(Long id, NotificationRequest notificationRequest);
    void deleteNotification(Long id);
}
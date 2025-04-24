package com.ecommerce.notification.service.impl;

import com.ecommerce.notification.dto.NotificationRequest;
import com.ecommerce.notification.dto.NotificationResponse;
import com.ecommerce.notification.exception.NotificationNotFoundException;
import com.ecommerce.notification.entity.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import com.ecommerce.notification.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public NotificationResponse createNotification(NotificationRequest notificationRequest) {
        Notification notification = new Notification();
        notification.setOrderId(notificationRequest.getOrderId());
        notification.setUserId(notificationRequest.getUserId());
        notification.setMessage(notificationRequest.getMessage());
        notification.setStatus(notificationRequest.getStatus());
        Notification savedNotification = notificationRepository.save(notification);
        return mapToNotificationResponse(savedNotification);
    }

    @Override
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponse getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        return mapToNotificationResponse(notification);
    }

    @Override
    public NotificationResponse updateNotification(Long id, NotificationRequest notificationRequest) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        notification.setOrderId(notificationRequest.getOrderId());
        notification.setUserId(notificationRequest.getUserId());
        notification.setMessage(notificationRequest.getMessage());
        notification.setStatus(notificationRequest.getStatus());
        Notification updatedNotification = notificationRepository.save(notification);
        return mapToNotificationResponse(updatedNotification);
    }

    @Override
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new NotificationNotFoundException(id);
        }
        notificationRepository.deleteById(id);
    }

    private NotificationResponse mapToNotificationResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setOrderId(notification.getOrderId());
        response.setUserId(notification.getUserId());
        response.setMessage(notification.getMessage());
        response.setStatus(notification.getStatus());
        return response;
    }
}
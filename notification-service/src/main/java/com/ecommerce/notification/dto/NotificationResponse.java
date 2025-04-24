package com.ecommerce.notification.dto;

import lombok.Data;

@Data
public class NotificationResponse {
    private Long id;
    private Long orderId;
    private Long userId;
    private String message;
    private String status;
}
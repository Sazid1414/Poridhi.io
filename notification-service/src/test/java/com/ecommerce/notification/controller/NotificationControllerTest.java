package com.ecommerce.notification.controller;

import com.ecommerce.notification.dto.NotificationRequest;
import com.ecommerce.notification.dto.NotificationResponse;
import com.ecommerce.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private NotificationRequest notificationRequest;
    private NotificationResponse notificationResponse;

    @BeforeEach
    void setUp() {
        notificationRequest = new NotificationRequest();
        notificationRequest.setOrderId(1L);
        notificationRequest.setUserId(1L);
        notificationRequest.setMessage("Order Confirmation");
        notificationRequest.setStatus("SENT");

        notificationResponse = new NotificationResponse();
        notificationResponse.setId(1L);
        notificationResponse.setOrderId(1L);
        notificationResponse.setUserId(1L);
        notificationResponse.setMessage("Order Confirmation");
        notificationResponse.setStatus("SENT");
    }

    @Test
    void createNotification_ReturnsCreatedNotification() throws Exception {
        when(notificationService.createNotification(any(NotificationRequest.class))).thenReturn(notificationResponse);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.message").value("Order Confirmation"));

        verify(notificationService, times(1)).createNotification(any(NotificationRequest.class));
    }

    @Test
    void getAllNotifications_ReturnsNotificationList() throws Exception {
        List<NotificationResponse> notifications = Arrays.asList(notificationResponse);
        when(notificationService.getAllNotifications()).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].message").value("Order Confirmation"));

        verify(notificationService, times(1)).getAllNotifications();
    }

    @Test
    void getNotificationById_ReturnsNotification() throws Exception {
        when(notificationService.getNotificationById(1L)).thenReturn(notificationResponse);

        mockMvc.perform(get("/api/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.message").value("Order Confirmation"));

        verify(notificationService, times(1)).getNotificationById(1L);
    }

    @Test
    void updateNotification_ReturnsUpdatedNotification() throws Exception {
        when(notificationService.updateNotification(eq(1L), any(NotificationRequest.class))).thenReturn(notificationResponse);

        mockMvc.perform(put("/api/notifications/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.message").value("Order Confirmation"));

        verify(notificationService, times(1)).updateNotification(eq(1L), any(NotificationRequest.class));
    }

    @Test
    void deleteNotification_ReturnsNoContent() throws Exception {
        doNothing().when(notificationService).deleteNotification(1L);

        mockMvc.perform(delete("/api/notifications/1"))
                .andExpect(status().isNoContent());

        verify(notificationService, times(1)).deleteNotification(1L);
    }
}
package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.dto.NotificationResponseDTO;
import com.secondproj.revconnect.model.Notification;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.NotificationRepository;
import com.secondproj.revconnect.security.CustomUserDetailsService;
import com.secondproj.revconnect.security.JwtUtil;
import com.secondproj.revconnect.service.NotificationService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private NotificationRepository notificationRepository;

    // 🔐 Required because of JWT security
    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    // =========================
    // GET NOTIFICATIONS
    // =========================
    @Test
    void testGetNotifications() throws Exception {

        User sender = new User();
        sender.setId(2L);
        sender.setUsername("senderUser");

        Notification notification = new Notification();
        notification.setId(1L);
        notification.setType("FOLLOW");
        notification.setMessage("Someone followed you");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setSender(sender);

        Mockito.when(notificationService.getNotifications(Mockito.any()))
                .thenReturn(List.of(notification));

        mockMvc.perform(get("/api/notifications")
                        .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].type").value("FOLLOW"))
                .andExpect(jsonPath("$[0].message").value("Someone followed you"))
                .andExpect(jsonPath("$[0].senderUsername").value("senderUser"));
    }

    // =========================
    // MARK AS READ
    // =========================
    @Test
    void testMarkNotificationRead() throws Exception {

        mockMvc.perform(put("/api/notifications/1/read")
                        .with(user("testuser"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Notification marked as read"));
    }

    // =========================
    // GET UNREAD COUNT
    // =========================
    @Test
    void testUnreadCount() throws Exception {

        Mockito.when(notificationService.getUnreadCount(Mockito.any()))
                .thenReturn(3L);

        mockMvc.perform(get("/api/notifications/unread-count")
                        .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    // =========================
    // DELETE NOTIFICATION
    // =========================
    @Test
    void testDeleteNotification() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        mockMvc.perform(delete("/api/notifications/1")
                        .with(user(user))   // ⭐ pass entity user
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification deleted"));
    }
}
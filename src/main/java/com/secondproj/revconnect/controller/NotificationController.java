package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.dto.NotificationResponseDTO;
import com.secondproj.revconnect.model.Notification;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @GetMapping
    public List<NotificationResponseDTO> getNotifications(
            @AuthenticationPrincipal User user
    ) {
        return notificationService.getNotifications(user)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private NotificationResponseDTO mapToDTO(Notification n) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(n.getId());
        dto.setType(n.getType());
        dto.setMessage(n.getMessage());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }

    @PutMapping("/{id}/read")
    public String markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "Notification marked as read";
    }
}
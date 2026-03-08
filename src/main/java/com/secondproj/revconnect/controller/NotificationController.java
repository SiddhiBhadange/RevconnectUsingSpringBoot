package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.dto.NotificationResponseDTO;
import com.secondproj.revconnect.model.Notification;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.NotificationRepository;
import com.secondproj.revconnect.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationRepository notificationRepository;
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

        // 🔥 ADD SENDER INFO
        if (n.getSender() != null) {
            dto.setSenderId(n.getSender().getId());
            dto.setSenderUsername(n.getSender().getUsername());
            dto.setSenderProfilePictureUrl(
                    n.getSender().getProfilePictureUrl()
            );
        }

        return dto;
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(Map.of("message", "Notification marked as read"));

    }
    @GetMapping("/unread-count")
    public long unreadCount(@AuthenticationPrincipal User user) {

        return notificationService.getUnreadCount(user);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        notificationRepository.deleteById(id);

        return ResponseEntity.ok("Notification deleted");
    }
}
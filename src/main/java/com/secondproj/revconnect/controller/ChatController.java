package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.model.Message;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.MessageRepository;
import com.secondproj.revconnect.repository.UserRepository;
import com.secondproj.revconnect.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // Send message
    @PostMapping("/{receiverId}")
    public Message sendMessage(
            @AuthenticationPrincipal User sender,
            @PathVariable Long receiverId,
            @RequestBody String content
    ) {

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);

        Message saved = messageRepository.save(message);

        //  CREATE CHAT NOTIFICATION
        if (!sender.getId().equals(receiver.getId())) {

            notificationService.createNotification(
                    receiver,                     // receiver
                    sender,                       // 🔥 sender (NEW)
                    "CHAT",
                    sender.getUsername() + " sent you a message"
            );
        }

        return saved;
    }

    // Get chat between two users
    @GetMapping("/{userId}")
    public List<Message> getChat(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long userId
    ) {

        User otherUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return messageRepository
                .findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
                        currentUser, otherUser,
                        otherUser, currentUser
                );
    }
}

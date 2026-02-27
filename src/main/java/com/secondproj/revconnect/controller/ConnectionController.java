package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.dto.ConnectionResponseDTO;
import com.secondproj.revconnect.model.Connection;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.UserRepository;
import com.secondproj.revconnect.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private UserRepository userRepository;

    // =============================
    // SEND CONNECTION REQUEST
    // =============================
    @PostMapping("/request/{userId}")
    public String sendRequest(
            Authentication authentication,
            @PathVariable Long userId
    ) {
        String username = authentication.getName();

        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (sender.getId().equals(userId)) {
            throw new RuntimeException("You cannot connect with yourself");
        }

        User receiver = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        connectionService.sendRequest(sender, receiver);

        return "Connection request sent";
    }

    // =============================
    // ACCEPT OR REJECT REQUEST
    // =============================
    @PostMapping("/respond/{requestId}")
    public String respond(
            Authentication authentication,
            @PathVariable Long requestId,
            @RequestParam String status
    ) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        connectionService.respondRequest(user, requestId, status);

        return "Request " + status;
    }

    // =============================
    // GET MY PENDING REQUESTS
    // =============================
    @GetMapping("/pending")
    public List<ConnectionResponseDTO> getPending(
            Authentication authentication
    ) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return connectionService.getPendingRequests(user)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // =============================
    // GET MY ACCEPTED CONNECTIONS
    // =============================
    @GetMapping
    public List<ConnectionResponseDTO> getConnections(
            Authentication authentication
    ) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return connectionService.getConnections(user)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // =============================
    // GET CONNECTION STATUS WITH USER
    // =============================
    @GetMapping("/status/{userId}")
    public ConnectionResponseDTO getStatus(
            Authentication authentication,
            @PathVariable Long userId
    ) {
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return connectionService.getConnectionStatus(currentUser, userId);
    }

    // =============================
    // REMOVE CONNECTION
    // =============================
    @DeleteMapping("/remove/{userId}")
    public String removeConnection(
            Authentication authentication,
            @PathVariable Long userId
    ) {
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        connectionService.removeConnection(currentUser, userId);

        return "Connection removed";
    }

    // =============================
    // DTO MAPPER
    // =============================
    private ConnectionResponseDTO mapToDTO(Connection connection) {

        ConnectionResponseDTO dto = new ConnectionResponseDTO();

        dto.setId(connection.getId());
        dto.setStatus(connection.getStatus());

        dto.setSenderId(connection.getSender().getId());
        dto.setSenderUsername(connection.getSender().getUsername());

        dto.setReceiverId(connection.getReceiver().getId());
        dto.setReceiverUsername(connection.getReceiver().getUsername());

        return dto;
    }
}
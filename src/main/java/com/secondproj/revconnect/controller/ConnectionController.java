package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.service.ConnectionService;
import com.secondproj.revconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private UserRepository userRepository;

    // 🔹 Send Connection Request
    @PostMapping("/request/{userId}")
    public String sendRequest(
            @AuthenticationPrincipal User user,
            @PathVariable Long userId
    ) {
        User receiver = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        connectionService.sendRequest(user, receiver);

        return "Connection request sent";
    }

    // 🔹 Accept / Reject Request
    @PostMapping("/respond/{requestId}")
    public String respond(
            @PathVariable Long requestId,
            @RequestParam String status
    ) {
        connectionService.respondRequest(requestId, status);
        return "Request " + status;
    }

    // 🔹 Get My Pending Requests
    @GetMapping("/pending")
    public List<?> getPendingRequests(
            @AuthenticationPrincipal User user
    ) {
        return connectionService.getPendingRequests(user);
    }

    // 🔹 Get My Connections
    @GetMapping
    public List<User> getConnections(
            @AuthenticationPrincipal User user
    ) {
        return connectionService.getConnections(user);
    }
}
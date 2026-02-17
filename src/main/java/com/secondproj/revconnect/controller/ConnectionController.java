package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.service.ConnectionService;
import com.secondproj.revconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private UserRepository userRepository;

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

    @PostMapping("/respond/{requestId}")
    public String respond(
            @PathVariable Long requestId,
            @RequestParam String status
    ) {
        connectionService.respondRequest(requestId, status);
        return "Request " + status;
    }
}
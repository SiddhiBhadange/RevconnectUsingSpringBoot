package com.secondproj.revconnect.service;

import com.secondproj.revconnect.model.Connection;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.ConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private NotificationService notificationService;

    // 🔹 Send Connection Request
    public void sendRequest(User sender, User receiver) {

        // Prevent self request
        if (sender.getId().equals(receiver.getId())) {
            throw new RuntimeException("You cannot connect with yourself");
        }

        // Prevent duplicate requests
        if (connectionRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new RuntimeException("Request already sent");
        }

        Connection connection = new Connection();
        connection.setSender(sender);
        connection.setReceiver(receiver);
        connection.setStatus("PENDING");

        connectionRepository.save(connection);

        // 🔥 Send Notification
        notificationService.createNotification(
                receiver,
                sender.getUsername() + " sent you a connection request",
                "CONNECTION_REQUEST"
        );
    }

    // 🔹 Respond to Request (ACCEPT / REJECT)
    public void respondRequest(Long requestId, String status) {

        Connection connection = connectionRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        connection.setStatus(status.toUpperCase());
        connectionRepository.save(connection);

        // If accepted → notify sender
        if ("ACCEPTED".equalsIgnoreCase(status)) {

            User sender = connection.getSender();
            User receiver = connection.getReceiver();

            notificationService.createNotification(
                    sender,
                    receiver.getUsername() + " accepted your connection request",
                    "CONNECTION_ACCEPTED"
            );
        }
    }

    // 🔹 Get Pending Requests
    public List<Connection> getPendingRequests(User user) {
        return connectionRepository.findByReceiverAndStatus(user, "PENDING");
    }

    // 🔹 Get My Connections
    public List<User> getConnections(User user) {
        return connectionRepository.findAcceptedConnections(user);
    }
}
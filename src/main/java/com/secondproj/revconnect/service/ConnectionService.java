package com.secondproj.revconnect.service;

import com.secondproj.revconnect.dto.ConnectionResponseDTO;
import com.secondproj.revconnect.model.Connection;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.ConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConnectionService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private NotificationService notificationService;

    // Send Connection Request
    public void sendRequest(User sender, User receiver) {

        // Prevent self request
        if (sender.getId().equals(receiver.getId())) {
            throw new RuntimeException("You cannot connect with yourself");
        }

        // Prevent duplicate requests
        List<Connection> existing =
                connectionRepository.findBetweenUsers(
                        sender.getId(),
                        receiver.getId()
                );

        if (!existing.isEmpty()) {
            throw new RuntimeException("Connection already exists");
        }

        Connection connection = new Connection();
        connection.setSender(sender);
        connection.setReceiver(receiver);
        connection.setStatus("PENDING");

        connectionRepository.save(connection);

        //  Send Notification
        notificationService.createNotification(
                receiver,
                "CONNECTION_REQUEST",
                sender.getUsername() + " sent you a connection request"

        );
    }

    //  Respond to Request (ACCEPT / REJECT)
    public void respondRequest(User currentUser, Long requestId, String status) {

        Connection connection = connectionRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // 🔒 Only receiver can respond
        if (!connection.getReceiver().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to respond");
        }

        connection.setStatus(status.toUpperCase());
        connectionRepository.save(connection);

        if ("ACCEPTED".equalsIgnoreCase(status)) {
            notificationService.createNotification(
                    connection.getSender(),
                    currentUser.getUsername() + " accepted your connection request",
                    "CONNECTION_ACCEPTED"
            );
        }
    }

    //  Get Pending Requests
    public List<Connection> getPendingRequests(User user) {
        return connectionRepository.findByReceiverAndStatus(user, "PENDING");
    }

    //  Get My Connections
    public List<Connection> getConnections(User user) {
        return connectionRepository.findBySenderAndStatus(user, "PENDING");
    }
    public ConnectionResponseDTO getConnectionStatus(User currentUser, Long targetUserId) {

        List<Connection> connections =
                connectionRepository.findBetweenUsers(
                        currentUser.getId(),
                        targetUserId
                );

        ConnectionResponseDTO dto = new ConnectionResponseDTO();

        if (connections.isEmpty()) {
            dto.setStatus("NONE");
            return dto;
        }

        // Just take the first one (safe even if duplicates exist)
        Connection connection = connections.get(0);
        dto.setId(connection.getId());

        if ("PENDING".equals(connection.getStatus())) {

            if (connection.getSender().getId().equals(currentUser.getId())) {
                dto.setStatus("PENDING_SENT");
            } else {
                dto.setStatus("PENDING_RECEIVED");
            }

        } else if ("ACCEPTED".equals(connection.getStatus())) {
            dto.setStatus("CONNECTED");
        }

        return dto;
    }
    public void removeConnection(User currentUser, Long targetUserId) {

        List<Connection> connections =
                connectionRepository.findBetweenUsers(
                        currentUser.getId(),
                        targetUserId
                );

        if (connections.isEmpty()) {
            throw new RuntimeException("No connection exists");
        }

        for (Connection connection : connections) {
            connectionRepository.delete(connection);
        }
    }
}
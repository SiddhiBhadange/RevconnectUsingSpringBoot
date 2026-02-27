package com.secondproj.revconnect.service;

import com.secondproj.revconnect.dto.ConnectionResponseDTO;
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

    // =============================
    // SEND CONNECTION REQUEST
    // =============================
    public void sendRequest(User sender, User receiver) {

        if (sender.getId().equals(receiver.getId())) {
            throw new RuntimeException("You cannot connect with yourself");
        }

        List<Connection> existing =
                connectionRepository.findBetweenUsers(
                        sender.getId(),
                        receiver.getId()
                );

        if (!existing.isEmpty()) {

            Connection connection = existing.get(0);

            // If already connected
            if ("ACCEPTED".equals(connection.getStatus())) {
                throw new RuntimeException("Already connected");
            }

            // If already pending
            if ("PENDING".equals(connection.getStatus())) {
                throw new RuntimeException("Request already pending");
            }

            // If rejected before → allow resend
            if ("REJECTED".equals(connection.getStatus())) {
                connection.setStatus("PENDING");
                connection.setSender(sender);
                connection.setReceiver(receiver);
                connectionRepository.save(connection);

                notificationService.createNotification(
                        receiver,
                        sender,
                        "CONNECTION_REQUEST",
                        sender.getUsername() + " sent you a connection request"
                );

                return;
            }
        }

        // Create new request
        Connection connection = new Connection();
        connection.setSender(sender);
        connection.setReceiver(receiver);
        connection.setStatus("PENDING");

        connectionRepository.save(connection);

        notificationService.createNotification(
                receiver,
                sender,
                "CONNECTION_REQUEST",
                sender.getUsername() + " sent you a connection request"
        );
    }

    // =============================
    // ACCEPT / REJECT REQUEST
    // =============================
    public void respondRequest(User currentUser, Long requestId, String status) {

        Connection connection = connectionRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!connection.getReceiver().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to respond");
        }

        connection.setStatus(status.toUpperCase());
        connectionRepository.save(connection);

        if ("ACCEPTED".equalsIgnoreCase(status)) {
            notificationService.createNotification(
                    connection.getSender(),
                    currentUser,
                    "CONNECTION_ACCEPTED",
                    currentUser.getUsername() + " accepted your connection request"
            );
        }
    }

    // =============================
    // GET PENDING RECEIVED REQUESTS
    // =============================
    public List<Connection> getPendingRequests(User user) {
        return connectionRepository.findByReceiverAndStatus(user, "PENDING");
    }

    // =============================
    // GET ACCEPTED CONNECTIONS
    // =============================
    public List<Connection> getConnections(User user) {

        return connectionRepository.findAll()
                .stream()
                .filter(c ->
                        "ACCEPTED".equals(c.getStatus()) &&
                                (c.getSender().getId().equals(user.getId()) ||
                                        c.getReceiver().getId().equals(user.getId()))
                )
                .toList();
    }

    // =============================
    // GET CONNECTION STATUS (UI)
    // =============================
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

        Connection connection = connections.get(0);
        dto.setId(connection.getId());

        String status = connection.getStatus();

        if ("PENDING".equals(status)) {

            if (connection.getSender().getId().equals(currentUser.getId())) {
                dto.setStatus("PENDING_SENT");
            } else {
                dto.setStatus("PENDING_RECEIVED");
            }

        } else if ("ACCEPTED".equals(status)) {
            dto.setStatus("CONNECTED");
        } else {
            dto.setStatus("NONE");
        }

        return dto;
    }

    // =============================
    // REMOVE CONNECTION
    // =============================
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
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

    public void sendRequest(User sender, User receiver) {

        if (connectionRepository.findBySenderAndReceiver(sender, receiver).isPresent()) {
            throw new RuntimeException("Request already sent");
        }

        Connection connection = new Connection();
        connection.setSender(sender);
        connection.setReceiver(receiver);
        connection.setStatus("PENDING");

        connectionRepository.save(connection);
    }

    public void respondRequest(Long requestId, String status) {

        Connection connection = connectionRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        connection.setStatus(status);
        connectionRepository.save(connection);
    }

    public List<Connection> getConnections(User user) {
        return connectionRepository.findBySenderOrReceiverAndStatus(user, user, "ACCEPTED");
    }
}
package com.secondproj.revconnect.repository;

import com.secondproj.revconnect.model.Connection;
import com.secondproj.revconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    Optional<Connection> findBySenderAndReceiver(User sender, User receiver);

    List<Connection> findByReceiverAndStatus(User receiver, String status);

    List<Connection> findBySenderAndStatus(User sender, String status);

    List<Connection> findBySenderOrReceiverAndStatus(User sender, User receiver, String status);
}
package com.secondproj.revconnect.repository;

import com.secondproj.revconnect.model.Connection;
import com.secondproj.revconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    boolean existsBySenderAndReceiver(User sender, User receiver);

    List<Connection> findByReceiverAndStatus(User receiver, String status);

    List<Connection> findBySenderAndStatus(User sender, String status);
}
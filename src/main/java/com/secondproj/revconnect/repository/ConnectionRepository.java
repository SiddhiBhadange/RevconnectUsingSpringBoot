package com.secondproj.revconnect.repository;

import com.secondproj.revconnect.model.Connection;
import com.secondproj.revconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    //  Check duplicate request (A → B)
    boolean existsBySenderAndReceiver(User sender, User receiver);
    //  Get pending requests received
    List<Connection> findByReceiverAndStatus(User receiver, String status);

    //  Get pending requests sent
    List<Connection> findBySenderAndStatus(User sender, String status);

    //  Get accepted connections (both sides)
    List<Connection> findBySenderOrReceiverAndStatus(
            User sender,
            User receiver,
            String status
    );

    //  Find connection between two users
    Optional<Connection> findBySenderAndReceiver(User sender, User receiver);

    // 🔹 Delete connection directly
    void deleteBySenderAndReceiver(User sender, User receiver);
}
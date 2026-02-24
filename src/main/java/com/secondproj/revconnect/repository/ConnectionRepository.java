package com.secondproj.revconnect.repository;

import com.secondproj.revconnect.model.Connection;
import com.secondproj.revconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    //  Delete connection directly
    void deleteBySenderAndReceiver(User sender, User receiver);

    @Query("""
SELECT c FROM Connection c
WHERE (c.sender.id = :user1 AND c.receiver.id = :user2)
   OR (c.sender.id = :user2 AND c.receiver.id = :user1)
""")
    List<Connection> findBetweenUsers(
            @Param("user1") Long user1,
            @Param("user2") Long user2
    );
}
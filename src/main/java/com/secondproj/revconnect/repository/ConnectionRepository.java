package com.secondproj.revconnect.repository;

import com.secondproj.revconnect.model.Connection;
import com.secondproj.revconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    //  Get pending requests received
    List<Connection> findByReceiverAndStatus(User receiver, String status);

    @Query("""
SELECT c FROM Connection c
WHERE (c.sender.id = :user1 AND c.receiver.id = :user2)
   OR (c.sender.id = :user2 AND c.receiver.id = :user1)
""")
    List<Connection> findBetweenUsers(
            @Param("user1") Long user1,
            @Param("user2") Long user2
    );

    long countByReceiverIdAndStatus(Long id, String accepted);
}
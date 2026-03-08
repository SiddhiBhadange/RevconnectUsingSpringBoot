package com.secondproj.revconnect.repository;

import com.secondproj.revconnect.model.Message;
import com.secondproj.revconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
            User sender1, User receiver1,
            User sender2, User receiver2
    );
}
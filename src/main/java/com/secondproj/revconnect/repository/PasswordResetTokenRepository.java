package com.secondproj.revconnect.repository;

import com.secondproj.revconnect.model.PasswordResetToken;
import com.secondproj.revconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);
}
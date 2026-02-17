package com.secondproj.revconnect.repository;

import com.secondproj.revconnect.model.Like;
import com.secondproj.revconnect.model.Post;
import com.secondproj.revconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndPost(User user, Post post);

    long countByPost(Post post);
}
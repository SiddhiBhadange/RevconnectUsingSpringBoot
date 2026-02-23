package com.secondproj.revconnect.repository;

import com.secondproj.revconnect.model.Like;
import com.secondproj.revconnect.model.Post;
import com.secondproj.revconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // 🔹 Check if user already liked post
    boolean existsByUserAndPost(User user, Post post);

    // 🔹 Find like (for unlike)
    Optional<Like> findByUserAndPost(User user, Post post);

    // 🔹 Count likes for a post (optional if you store likeCount in Post)
    long countByPost(Post post);

    // 🔹 Delete like directly (optional)
    void deleteByUserAndPost(User user, Post post);
}
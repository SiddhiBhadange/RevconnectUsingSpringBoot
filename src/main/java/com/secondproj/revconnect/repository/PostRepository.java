package com.secondproj.revconnect.repository;

import com.secondproj.revconnect.model.Post;
import com.secondproj.revconnect.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUser(User user);

    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> getPostsByUserId(Long userId);

    @Transactional
    void deleteByUserId(Long userId);

    long countByUser(User user);
}
package com.secondproj.revconnect.repository;
import com.secondproj.revconnect.model.Comment;
import com.secondproj.revconnect.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    long countByPost(Post post);
    void deleteByPost(Post post);
}
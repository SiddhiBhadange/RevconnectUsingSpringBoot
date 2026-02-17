package com.secondproj.revconnect.repository;

import com.secondproj.revconnect.model.Post;
import com.secondproj.revconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUser(User user);

    List<Post> findByHashtagsContaining(String hashtag);
}
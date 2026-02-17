package com.secondproj.revconnect.service;

import com.secondproj.revconnect.model.Like;
import com.secondproj.revconnect.model.Post;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.LikeRepository;
import com.secondproj.revconnect.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    public void likePost(User user, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (likeRepository.findByUserAndPost(user, post).isPresent()) {
            throw new RuntimeException("Already liked");
        }

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);

        likeRepository.save(like);
    }

    public void unlikePost(User user, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Like like = likeRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new RuntimeException("Like not found"));

        likeRepository.delete(like);
    }
}
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

    @Autowired
    private NotificationService  notificationService;
    public boolean likePost(User user, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (likeRepository.existsByUserAndPost(user, post)) {
            return false;
        }

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        likeRepository.save(like);

        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);

        // send notification only on like
        if (!post.getUser().getId().equals(user.getId())) {
            notificationService.createNotification(
                    post.getUser(),   // receiver
                    user,             // sender
                    "LIKE",
                    user.getUsername() + " liked your post"
            );
        }


        return true;
    }

    public boolean unlikePost(User user, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Like like = likeRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new RuntimeException("Like not found"));

        likeRepository.delete(like);

        post.setLikeCount(post.getLikeCount() - 1);
        postRepository.save(post);

        return true;
    }
}
package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.model.Like;
import com.secondproj.revconnect.model.Post;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.LikeRepository;
import com.secondproj.revconnect.repository.PostRepository;
import com.secondproj.revconnect.service.LikeService;
import com.secondproj.revconnect.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PostRepository postRepository;

    @PostMapping("/{postId}")
    public ResponseEntity<Boolean> like(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId
    ) {
        boolean liked = likeService.likePost(user, postId);
        return ResponseEntity.ok(liked);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Boolean> unlike(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId
    ) {
        boolean unliked = likeService.unlikePost(user, postId);
        return ResponseEntity.ok(unliked);
    }
    public boolean likePost(User user, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean liked = likeRepository.existsByUserAndPost(user, post);

        if (!liked) {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);

            // 🔥 CREATE NOTIFICATION (avoid self-like)
            if (!post.getUser().getId().equals(user.getId())) {

                notificationService.createNotification(
                        post.getUser(),          // receiver (post owner)
                        user,                    // 🔥 sender (who liked)
                        "LIKE",
                        user.getUsername() + " liked your post"
                );
            }

            return true;
        }

        return false;
    }
}
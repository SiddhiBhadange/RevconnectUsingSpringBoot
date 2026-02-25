package com.secondproj.revconnect.service;

import com.secondproj.revconnect.model.Comment;
import com.secondproj.revconnect.model.Post;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.CommentRepository;
import com.secondproj.revconnect.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PostRepository postRepository;


    public Comment addComment(User user, Long postId, String content) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        // 🔥 Create notification (avoid self notification)
        if (!post.getUser().getId().equals(user.getId())) {
            notificationService.createNotification(
                    post.getUser(),
                    user,
                    "COMMENT",
                    user.getUsername() + " commented on your post"
            );
        }

        return savedComment;
    }

    public List<Comment> getComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }
}
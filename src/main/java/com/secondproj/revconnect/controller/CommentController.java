package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.dto.CommentRequestDTO;
import com.secondproj.revconnect.dto.CommentResponseDTO;
import com.secondproj.revconnect.model.Comment;
import com.secondproj.revconnect.model.Post;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.CommentRepository;
import com.secondproj.revconnect.repository.PostRepository;
import com.secondproj.revconnect.service.CommentService;
import com.secondproj.revconnect.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PostRepository postRepository;


    private CommentResponseDTO mapToCommentDTO(Comment comment) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        return dto;
    }


    // VIEW COMMENTS
    @GetMapping("/{postId}")
    public List<CommentResponseDTO> getComments(@PathVariable Long postId) {

        return commentService.getComments(postId)
                .stream()
                .map(this::mapToCommentDTO)
                .toList();
    }
    @PostMapping("/{postId}")
    public CommentResponseDTO addComment(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId,
            @RequestBody CommentRequestDTO request
    ) {

        Comment savedComment = commentService.addComment(
                user,
                postId,
                request.getContent()
        );

        return mapToCommentDTO(savedComment);
    }
}
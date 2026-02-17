package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.dto.CommentRequestDTO;
import com.secondproj.revconnect.dto.CommentResponseDTO;
import com.secondproj.revconnect.model.Comment;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;
    // ADD COMMENT
    @PostMapping("/{postId}")
    public CommentResponseDTO addComment(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId,
            @RequestBody CommentRequestDTO dto
    ) {
        Comment comment = commentService.addComment(user, postId, dto.getContent());
        return mapToCommentDTO(comment);
    }

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
    public List<Comment> getComments(@PathVariable Long postId) {
        return commentService.getComments(postId);
    }
}
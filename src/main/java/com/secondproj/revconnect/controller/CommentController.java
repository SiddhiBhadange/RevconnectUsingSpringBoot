package com.secondproj.revconnect.controller;

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
    public Comment addComment(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId,
            @RequestParam String content
    ) {
        return commentService.addComment(user, postId, content);
    }

    // VIEW COMMENTS
    @GetMapping("/{postId}")
    public List<Comment> getComments(@PathVariable Long postId) {
        return commentService.getComments(postId);
    }
}
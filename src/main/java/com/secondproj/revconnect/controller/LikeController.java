package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/{postId}")
    public String like(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId
    ) {
        likeService.likePost(user, postId);
        return "Post liked";
    }

    @DeleteMapping("/{postId}")
    public String unlike(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId
    ) {
        likeService.unlikePost(user, postId);
        return "Post unliked";
    }
}
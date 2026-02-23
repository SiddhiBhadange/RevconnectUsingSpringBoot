package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

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
}
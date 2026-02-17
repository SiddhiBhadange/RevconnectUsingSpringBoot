package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.UserRepository;
import com.secondproj.revconnect.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{userId}")
    public String follow(
            @AuthenticationPrincipal User user,
            @PathVariable Long userId
    ) {
        User following = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        followService.follow(user, following);
        return "Followed successfully";
    }

    @DeleteMapping("/{userId}")
    public String unfollow(
            @AuthenticationPrincipal User user,
            @PathVariable Long userId
    ) {
        User following = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        followService.unfollow(user, following);
        return "Unfollowed successfully";
    }
}
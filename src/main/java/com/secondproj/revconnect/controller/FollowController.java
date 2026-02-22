package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.model.Follow;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.UserRepository;
import com.secondproj.revconnect.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("/{userId}/followers")
    public List<User> getFollowers(@PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followService.getFollowers(user)
                .stream()
                .map(Follow::getFollower)
                .toList();
    }

    @GetMapping("/{userId}/following")
    public List<User> getFollowing(@PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followService.getFollowing(user)
                .stream()
                .map(Follow::getFollowing)
                .toList();
    }

    @GetMapping("/{userId}/followers/count")
    public long getFollowersCount(@PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followService.getFollowersCount(user);
    }

    @GetMapping("/{userId}/following/count")
    public long getFollowingCount(@PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followService.getFollowingCount(user);
    }

    @GetMapping("/{userId}/is-following")
    public boolean isFollowing(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long userId
    ) {
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followService.isFollowing(currentUser, target);
    }
}
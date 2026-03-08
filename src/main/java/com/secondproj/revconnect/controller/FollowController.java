package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.dto.UserResponseDTO;
import com.secondproj.revconnect.model.Follow;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.FollowRepository;
import com.secondproj.revconnect.repository.NotificationRepository;
import com.secondproj.revconnect.repository.UserRepository;
import com.secondproj.revconnect.service.FollowService;
import com.secondproj.revconnect.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FollowRepository  followRepository;
    //  FOLLOW USER
    @PostMapping("/{userId}")
    public String follow(
            @AuthenticationPrincipal User user,
            @PathVariable Long userId
    ) {

        // Prevent self-follow
        if (user.getId().equals(userId)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        User following = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //  SAVE FOLLOW RELATION
        followService.follow(user, following);

        // CREATE NOTIFICATION
        notificationService.createNotification(
                following,          // receiver
                user,               // 🔥 sender
                "FOLLOW",
                user.getUsername() + " started following you"
        );

        return "Followed successfully";
    }

    //  UNFOLLOW USER
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

//    //  GET FOLLOWERS
//    @GetMapping("/{userId}/followers")
//    public List<User> getFollowers(@PathVariable Long userId) {
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        return followService.getFollowers(user)
//                .stream()
//                .map(Follow::getFollower)
//                .toList();
//    }
//
//    // GET FOLLOWING
//    @GetMapping("/{userId}/following")
//    public List<User> getFollowing(@PathVariable Long userId) {
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        return followService.getFollowing(user)
//                .stream()
//                .map(Follow::getFollowing)
//                .toList();
//    }

    //  FOLLOWERS COUNT
    @GetMapping("/{userId}/followers/count")
    public long getFollowersCount(@PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followService.getFollowersCount(user);
    }

    // FOLLOWING COUNT
    @GetMapping("/{userId}/following/count")
    public long getFollowingCount(@PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followService.getFollowingCount(user);
    }

    // CHECK FOLLOW STATUS
    @GetMapping("/{userId}/is-following")
    public boolean isFollowing(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long userId
    ) {

        if (currentUser == null) return false;

        User target = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followService.isFollowing(currentUser, target);
    }
    @DeleteMapping("/remove/{userId}")
    public ResponseEntity<String> removeFollower(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {

        followRepository.deleteByFollowerIdAndFollowingId(
                currentUser.getId(), userId);

        return ResponseEntity.ok("Unfollowed successfully");
    }
    @GetMapping("/followers/{userId}")
    public List<UserResponseDTO> getFollowers(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {

        return followRepository.findFollowersByUserId(userId)
                .stream()
                .map(user -> mapToUserDTO(user, currentUser))
                .toList();
    }

    @GetMapping("/following/{userId}")
    public List<UserResponseDTO> getFollowing(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {

        return followRepository.findFollowingByUserId(userId)
                .stream()
                .map(user -> mapToUserDTO(user, currentUser))
                .toList();
    }
    private UserResponseDTO mapToUserDTO(User user, User currentUser) {

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());

        // CHECK IF CURRENT USER FOLLOWS THIS USER
        if (currentUser != null) {
            boolean isFollowing = followRepository
                    .existsByFollowerAndFollowing(currentUser, user);

            dto.setFollowing(isFollowing);
        } else {
            dto.setFollowing(false);
        }

        return dto;
    }
}
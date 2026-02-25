package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.dto.UserResponseDTO;
import com.secondproj.revconnect.model.Role;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.*;
import com.secondproj.revconnect.service.PostService;
import com.secondproj.revconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private ConnectionRepository connectionRepository;
    @Autowired
    private FollowRepository followRepository;

    // 🔎 Search by username
    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDTO>> search(@RequestParam String keyword) {

        List<User> users = userService.searchUsers(keyword);

        List<UserResponseDTO> response = users.stream()
                .map(this::mapToUserDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    // ✅ Get logged-in user
    @GetMapping("/me")
    public UserResponseDTO getMyProfile(@AuthenticationPrincipal User user) {

        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }

        return mapToUserDTO(user);
    }

    // ✅ Update logged-in user
    @PutMapping("/me")
    public UserResponseDTO updateProfile(
            @AuthenticationPrincipal User currentUser,
            @RequestBody UserResponseDTO dto
    ) {

        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        // Basic fields
        currentUser.setName(dto.getName());
        currentUser.setBio(dto.getBio());
        currentUser.setLocation(dto.getLocation());
        currentUser.setWebsite(dto.getWebsite());
        currentUser.setProfilePictureUrl(dto.getProfilePictureUrl());

        // 🔥 UPDATE ROLE
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            currentUser.setRoles(dto.getRoles());
        }

        // 🔥 BUSINESS FIELDS
        if (currentUser.getRoles().contains(Role.BUSINESS) ||
                currentUser.getRoles().contains(Role.CREATOR)) {

            currentUser.setBusinessCategory(dto.getBusinessCategory());
            currentUser.setBusinessAddress(dto.getBusinessAddress());
            currentUser.setBusinessHours(dto.getBusinessHours());
            currentUser.setContactInfo(dto.getContactInfo());

        } else {
            // Clear if personal
            currentUser.setBusinessCategory(null);
            currentUser.setBusinessAddress(null);
            currentUser.setBusinessHours(null);
            currentUser.setContactInfo(null);
        }

        User savedUser = userRepository.save(currentUser);

        return mapToUserDTO(savedUser);
    }
    // ✅ Get any user by ID (view profile)
    @GetMapping("/{userId:\\d+}")
    public UserResponseDTO getUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToUserDTO(user);
    }

    // 🔁 ENTITY → DTO
    private UserResponseDTO mapToUserDTO(User user) {

        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setRoles(user.getRoles());
        dto.setName(user.getName());
        dto.setBio(user.getBio());
        dto.setLocation(user.getLocation());
        dto.setWebsite(user.getWebsite());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());

        // 🔥 ADD THESE
        dto.setBusinessCategory(user.getBusinessCategory());
        dto.setBusinessAddress(user.getBusinessAddress());
        dto.setBusinessHours(user.getBusinessHours());
        dto.setContactInfo(user.getContactInfo());

        return dto;
    }
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMyProfile(@AuthenticationPrincipal User currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
//        connectionRepository.deleteBySenderId(currentUser.getId());
//        connectionRepository.deleteByReceiverId(currentUser.getId());
        // Delete posts first
        postRepository.deleteByUserId(currentUser.getId());

        // Delete notifications
        notificationRepository.deleteByUserId(currentUser.getId());

        // Delete follows
        followRepository.deleteByFollowerId(currentUser.getId());
        followRepository.deleteByFollowingId(currentUser.getId());

        // Then delete user
        userRepository.deleteById(currentUser.getId());

        return ResponseEntity.ok("Profile deleted successfully");
    }

}
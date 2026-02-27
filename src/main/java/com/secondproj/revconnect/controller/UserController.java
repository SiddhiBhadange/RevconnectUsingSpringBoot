package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.dto.UserResponseDTO;
import com.secondproj.revconnect.model.Role;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.*;
import com.secondproj.revconnect.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private FollowRepository followRepository;

    /*  SEARCH USERS */


    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDTO>> search(
            @RequestParam String keyword
    ) {

        List<User> users = userService.searchUsers(keyword);

        List<UserResponseDTO> response = users.stream()
                .map(this::mapToUserDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    /* ============================================= */
    /* 👤 GET LOGGED-IN USER */
    /* ============================================= */

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile(
            Authentication authentication
    ) {

        if (authentication == null) {
            return ResponseEntity.status(403).build();
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(mapToUserDTO(user));
    }

    /* ============================================= */
    /* ✏ UPDATE PROFILE */
    /* ============================================= */

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateProfile(
            Authentication authentication,
            @RequestBody UserResponseDTO dto
    ) {

        if (authentication == null) {
            return ResponseEntity.status(403).build();
        }

        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Basic fields
        currentUser.setName(dto.getName());
        currentUser.setBio(dto.getBio());
        currentUser.setLocation(dto.getLocation());
        currentUser.setWebsite(dto.getWebsite());
        currentUser.setProfilePictureUrl(dto.getProfilePictureUrl());

        // Role update
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            currentUser.setRoles(dto.getRoles());
        }

        // Business / Creator fields
        if (currentUser.getRoles().contains(Role.BUSINESS) ||
                currentUser.getRoles().contains(Role.CREATOR)) {

            currentUser.setBusinessCategory(dto.getBusinessCategory());
            currentUser.setBusinessAddress(dto.getBusinessAddress());
            currentUser.setBusinessHours(dto.getBusinessHours());
            currentUser.setContactInfo(dto.getContactInfo());

        } else {
            currentUser.setBusinessCategory(null);
            currentUser.setBusinessAddress(null);
            currentUser.setBusinessHours(null);
            currentUser.setContactInfo(null);
        }

        User savedUser = userRepository.save(currentUser);

        return ResponseEntity.ok(mapToUserDTO(savedUser));
    }

    /* ============================================= */
    /* 👁 VIEW ANY USER BY ID */
    /* ============================================= */

    @GetMapping("/{userId:\\d+}")
    public ResponseEntity<UserResponseDTO> getUser(
            @PathVariable Long userId
    ) {

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(mapToUserDTO(user));
    }

    /* ============================================= */
    /* 🗑 DELETE OWN PROFILE */
    /* ============================================= */

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile(
            Authentication authentication
    ) {

        if (authentication == null) {
            return ResponseEntity.status(403).build();
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();

        // Delete related data first
        postRepository.deleteByUserId(userId);
        notificationRepository.deleteByUserId(userId);
        followRepository.deleteByFollowerId(userId);
        followRepository.deleteByFollowingId(userId);

        // Delete user
        userRepository.deleteById(userId);

        return ResponseEntity.noContent().build();
    }

    /* ============================================= */
    /* 🔁 ENTITY → DTO */
    /* ============================================= */

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

        dto.setBusinessCategory(user.getBusinessCategory());
        dto.setBusinessAddress(user.getBusinessAddress());
        dto.setBusinessHours(user.getBusinessHours());
        dto.setContactInfo(user.getContactInfo());

        return dto;
    }
}
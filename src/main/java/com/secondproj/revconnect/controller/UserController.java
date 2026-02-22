package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.dto.UserResponseDTO;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.UserRepository;
import com.secondproj.revconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // 🔎 Search by username
    @GetMapping("/search")
    public UserResponseDTO search(@RequestParam String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserDTO(user);
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

        currentUser.setName(dto.getName());
        currentUser.setBio(dto.getBio());
        currentUser.setLocation(dto.getLocation());
        currentUser.setWebsite(dto.getWebsite());
        currentUser.setProfilePictureUrl(dto.getProfilePictureUrl());

        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            currentUser.setRoles(dto.getRoles());
        }

        User savedUser = userRepository.save(currentUser);

        return mapToUserDTO(savedUser);
    }

    // ✅ Get any user by ID (view profile)
    @GetMapping("/{userId}")
    public Optional<User> getUser(@PathVariable Long userId) {
        return userService.getUserById(userId);
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

        return dto;
    }
}
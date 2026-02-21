package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.dto.UserResponseDTO;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.UserRepository;
import com.secondproj.revconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/search")
    public UserResponseDTO search(@RequestParam String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserDTO(user);
    }

    @GetMapping("/me")
    public UserResponseDTO getMyProfile(@AuthenticationPrincipal User user) {
        return mapToUserDTO(user);
    }

    @GetMapping("/api/users/me")
    public UserResponseDTO getCurrentUser(@AuthenticationPrincipal User user) {
        return new UserResponseDTO(user.getId(), user.getUsername());
    }

    @PutMapping("/me")
    public UserResponseDTO updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UserResponseDTO dto
    ) {
        user.setName(dto.getName());
        user.setBio(dto.getBio());
        user.setLocation(dto.getLocation());
        user.setWebsite(dto.getWebsite());

        User updated = userService.updateProfile(user);
        return mapToUserDTO(updated);
    }

    // ✅ ENTITY → DTO MAPPER (INSIDE CLASS)
    private UserResponseDTO mapToUserDTO(User user) {

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setRoles(user.getRoles()); // correct
        dto.setName(user.getName());
        dto.setBio(user.getBio());
        dto.setLocation(user.getLocation());
        dto.setWebsite(user.getWebsite());

        return dto;
    }
    @GetMapping("/{userId}")
    public UserResponseDTO getUser(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }
}
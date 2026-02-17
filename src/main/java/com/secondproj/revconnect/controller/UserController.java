package com.secondproj.revconnect.controller;

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

    // VIEW MY PROFILE
    @GetMapping("/me")
    public User getMyProfile(@AuthenticationPrincipal User user) {
        return user;
    }

    // UPDATE PROFILE
    @PutMapping("/me")
    public User updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody User updatedUser
    ) {
        user.setName(updatedUser.getName());
        user.setBio(updatedUser.getBio());
        user.setLocation(updatedUser.getLocation());
        user.setWebsite(updatedUser.getWebsite());
        return userService.updateProfile(user);
    }

    // SEARCH USER
    @GetMapping("/search")
    public User search(@RequestParam String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.model.Role;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // REGISTER
    @PostMapping("/register")
    public User register(
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam Role role
    ) {
        return userService.registerUser(email, username, password, role);
    }

    // LOGIN will be handled by JWT (next step)
}
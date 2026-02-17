package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.dto.LoginRequestDTO;
import com.secondproj.revconnect.dto.TokenResponseDTO;
import com.secondproj.revconnect.model.Role;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.security.JwtUtil;
import com.secondproj.revconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

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
    @PostMapping("/login")
    public TokenResponseDTO login(@RequestBody LoginRequestDTO dto) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                dto.getUsernameOrEmail(),
                                dto.getPassword()
                        )
                );

        String token = jwtUtil.generateToken(dto.getUsernameOrEmail());

        return new TokenResponseDTO(token);
    }
}
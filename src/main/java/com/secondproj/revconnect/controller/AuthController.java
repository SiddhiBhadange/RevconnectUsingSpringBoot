package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.dto.LoginRequestDTO;
import com.secondproj.revconnect.dto.RegisterRequestDTO;
import com.secondproj.revconnect.dto.TokenResponseDTO;
import com.secondproj.revconnect.model.Role;
import com.secondproj.revconnect.security.JwtUtil;
import com.secondproj.revconnect.service.PasswordResetService;
import com.secondproj.revconnect.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200") // No allowCredentials needed
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordResetService passwordResetService;

    // =========================
    // REGISTER
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO dto) {

        userService.registerUser(
                dto.getEmail(),
                dto.getUsername(),
                dto.getPassword(),
                Role.PERSONAL
        );

        return ResponseEntity.ok("User registered successfully");
    }

    // =========================
    // LOGIN (Returns JWT in body)
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    dto.getUsernameOrEmail(),
                                    dto.getPassword()
                            )
                    );

            // 🔥 Use authenticated principal
            UserDetails userDetails =
                    (UserDetails) authentication.getPrincipal();

            String token =
                    jwtUtil.generateToken(userDetails.getUsername());

            return ResponseEntity.ok(
                    new TokenResponseDTO(token)
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(401)
                    .body("Invalid username/email or password");
        }
    }

    // =========================
    // LOGOUT (Frontend handles token removal)
    // =========================
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("Logged out successfully");
    }

    // =========================
    // FORGOT PASSWORD
    // =========================
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody Map<String, String> request
    ) {

        String email = request.get("email");

        passwordResetService.createPasswordResetToken(email);

        return ResponseEntity.ok("Reset link sent");
    }

    // =========================
    // RESET PASSWORD
    // =========================
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody Map<String, String> request
    ) {

        passwordResetService.resetPassword(
                request.get("token"),
                request.get("newPassword")
        );

        return ResponseEntity.ok("Password updated successfully");
    }
}
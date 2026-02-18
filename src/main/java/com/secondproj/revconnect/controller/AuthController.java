package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.dto.LoginRequestDTO;
import com.secondproj.revconnect.dto.RegisterRequestDTO;
import com.secondproj.revconnect.dto.TokenResponseDTO;
import com.secondproj.revconnect.model.Role;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.security.JwtUtil;
import com.secondproj.revconnect.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
        origins = "http://localhost:4200",
        allowCredentials = "true"
)
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ REGISTER (role decided by backend)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO dto) {

        userService.registerUser(
                dto.getEmail(),
                dto.getUsername(),
                dto.getPassword(),
                Role.PERSONAL   // ✅ default role
        );

        return ResponseEntity.ok().build();
    }

    // ✅ LOGIN (JWT in HttpOnly Cookie)
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequestDTO dto,
            HttpServletResponse response) {

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    dto.getUsernameOrEmail(),
                                    dto.getPassword()
                            )
                    );

            String token = jwtUtil.generateToken(dto.getUsernameOrEmail());

            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);

            response.addCookie(cookie);

            return ResponseEntity.ok().body("Login successful");

        } catch (Exception e) {
            return ResponseEntity
                    .status(401)
                    .body("Invalid username/email or password");
        }
    }

    // ✅ LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }
}

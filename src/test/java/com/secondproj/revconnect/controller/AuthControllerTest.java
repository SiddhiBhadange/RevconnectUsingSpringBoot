package com.secondproj.revconnect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondproj.revconnect.dto.LoginRequestDTO;
import com.secondproj.revconnect.dto.RegisterRequestDTO;
import com.secondproj.revconnect.security.JwtAuthenticationFilter;
import com.secondproj.revconnect.security.JwtUtil;
import com.secondproj.revconnect.security.CustomUserDetailsService;
import com.secondproj.revconnect.service.PasswordResetService;
import com.secondproj.revconnect.service.UserService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private PasswordResetService passwordResetService;

    // ✅ FIX
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    // ================= REGISTER TEST =================
    @Test
    void testRegister() throws Exception {

        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail("test@mail.com");
        dto.setUsername("testuser");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    // ================= LOGIN TEST =================
    @Test
    void testLogin() throws Exception {

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsernameOrEmail("testuser");
        dto.setPassword("password123");

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        new User("testuser", "password", Collections.emptyList()),
                        null
                );

        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(authentication);

        Mockito.when(jwtUtil.generateToken("testuser"))
                .thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    // ================= FORGOT PASSWORD TEST =================
    @Test
    void testForgotPassword() throws Exception {

        Map<String, String> request = Map.of(
                "email", "test@mail.com"
        );

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Reset link sent"));
    }

    // ================= RESET PASSWORD TEST =================
    @Test
    void testResetPassword() throws Exception {

        Map<String, String> request = Map.of(
                "token", "abc123",
                "newPassword", "newpass123"
        );

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password updated successfully"));
    }
}
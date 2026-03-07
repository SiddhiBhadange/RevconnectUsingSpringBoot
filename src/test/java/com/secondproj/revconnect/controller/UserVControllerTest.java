package com.secondproj.revconnect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondproj.revconnect.dto.UserResponseDTO;
import com.secondproj.revconnect.model.Role;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.*;
import com.secondproj.revconnect.security.CustomUserDetailsService;
import com.secondproj.revconnect.security.JwtUtil;
import com.secondproj.revconnect.service.UserService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private FollowRepository followRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    // =========================
    // SEARCH USERS
    // =========================
    @Test
    void testSearchUsers() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        Mockito.when(userService.searchUsers("jo"))
                .thenReturn(List.of(user));

        mockMvc.perform(get("/api/users/search")
                        .param("keyword", "jo")
                        .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("john"));
    }

    // =========================
    // GET MY PROFILE
    // =========================
    @Test
    void testGetMyProfile() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@mail.com");

        Mockito.when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/me")
                        .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    // =========================
    // UPDATE PROFILE
    // =========================
    @Test
    void testUpdateProfile() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        UserResponseDTO dto = new UserResponseDTO();
        dto.setName("Updated Name");
        dto.setBio("New bio");
        dto.setRoles(Set.of(Role.PERSONAL));

        Mockito.when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(user);

        mockMvc.perform(put("/api/users/me")
                        .with(user("testuser"))
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    // =========================
    // GET USER BY ID
    // =========================
    @Test
    void testGetUserById() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        Mockito.when(userService.getUserById(1L))
                .thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1")
                        .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"));
    }

    // =========================
    // DELETE PROFILE
    // =========================
    @Test
    void testDeleteProfile() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Mockito.when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/users/me")
                        .with(user("testuser"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
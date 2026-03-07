package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.LikeRepository;
import com.secondproj.revconnect.repository.PostRepository;
import com.secondproj.revconnect.security.CustomUserDetailsService;
import com.secondproj.revconnect.security.JwtUtil;
import com.secondproj.revconnect.service.LikeService;
import com.secondproj.revconnect.service.NotificationService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LikeController.class)
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeService likeService;

    @MockBean
    private LikeRepository likeRepository;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private JwtUtil jwtUtil;

    // ⭐ ADD THIS (fixes your current error)
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void testLikePost() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Mockito.when(likeService.likePost(Mockito.any(), Mockito.eq(1L)))
                .thenReturn(true);

        mockMvc.perform(post("/api/likes/1")
                        .with(user(user))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testUnlikePost() throws Exception {

        User user = new User();
        user.setId(1L);

        Mockito.when(likeService.unlikePost(Mockito.any(), Mockito.eq(1L)))
                .thenReturn(true);

        mockMvc.perform(delete("/api/likes/1")
                        .with(user(user))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
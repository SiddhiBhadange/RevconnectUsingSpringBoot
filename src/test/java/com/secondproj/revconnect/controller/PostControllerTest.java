package com.secondproj.revconnect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondproj.revconnect.dto.PostRequestDTO;
import com.secondproj.revconnect.model.Post;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.CommentRepository;
import com.secondproj.revconnect.repository.LikeRepository;
import com.secondproj.revconnect.security.CustomUserDetailsService;
import com.secondproj.revconnect.security.JwtUtil;
import com.secondproj.revconnect.service.PostService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private LikeRepository likeRepository;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    // =========================
    // CREATE POST
    // =========================
    @Test
    void testCreatePost() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Post post = new Post();
        post.setId(1L);
        post.setContent("Hello world");
        post.setHashtags("#spring");
        post.setCreatedAt(LocalDateTime.now());
        post.setUser(user);

        PostRequestDTO request = new PostRequestDTO();
        request.setContent("Hello world");
        request.setHashtags("#spring");

        Mockito.when(postService.createPost(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(post);

        Mockito.when(likeRepository.countByPost(Mockito.any())).thenReturn(0L);
        Mockito.when(commentRepository.countByPost(Mockito.any())).thenReturn(0L);
        Mockito.when(likeRepository.existsByUserAndPost(Mockito.any(), Mockito.any()))
                .thenReturn(false);

        mockMvc.perform(post("/api/posts")
                        .with(user(user))
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Hello world"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    // =========================
    // GET ALL POSTS
    // =========================
    @Test
    void testGetAllPosts() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Post post = new Post();
        post.setId(1L);
        post.setContent("Test post");
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());

        Mockito.when(postService.getAllPosts()).thenReturn(List.of(post));
        Mockito.when(likeRepository.countByPost(Mockito.any())).thenReturn(2L);
        Mockito.when(commentRepository.countByPost(Mockito.any())).thenReturn(1L);
        Mockito.when(likeRepository.existsByUserAndPost(Mockito.any(), Mockito.any()))
                .thenReturn(true);

        mockMvc.perform(get("/api/posts")
                        .with(user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test post"))
                .andExpect(jsonPath("$[0].likeCount").value(2));
    }

    // =========================
    // GET MY POSTS
    // =========================
    @Test
    void testMyPosts() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Post post = new Post();
        post.setId(1L);
        post.setContent("My post");
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());

        Mockito.when(postService.getMyPosts(Mockito.any()))
                .thenReturn(List.of(post));

        Mockito.when(likeRepository.countByPost(Mockito.any())).thenReturn(0L);
        Mockito.when(commentRepository.countByPost(Mockito.any())).thenReturn(0L);
        Mockito.when(likeRepository.existsByUserAndPost(Mockito.any(), Mockito.any()))
                .thenReturn(false);

        mockMvc.perform(get("/api/posts/me")
                        .with(user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("My post"));
    }

    // =========================
    // DELETE POST
    // =========================
    @Test
    void testDeletePost() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        mockMvc.perform(delete("/api/posts/1")
                        .with(user(user))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Post deleted successfully"));
    }
}
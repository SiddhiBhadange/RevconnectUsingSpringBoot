package com.secondproj.revconnect.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondproj.revconnect.dto.CommentRequestDTO;
import com.secondproj.revconnect.model.Comment;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.CommentRepository;
import com.secondproj.revconnect.repository.PostRepository;
import com.secondproj.revconnect.security.JwtAuthenticationFilter;
import com.secondproj.revconnect.security.JwtUtil;
import com.secondproj.revconnect.service.CommentService;
import com.secondproj.revconnect.service.NotificationService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    // 🔥 Required to avoid security bean errors
    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private PostRepository postRepository;

    // =========================
    // TEST GET COMMENTS
    // =========================
    @Test
    void testGetComments() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Nice post");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(user);

        Mockito.when(commentService.getComments(10L))
                .thenReturn(List.of(comment));

        mockMvc.perform(get("/api/comments/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Nice post"))
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    // =========================
    // TEST ADD COMMENT
    // =========================
    @Test
    void testAddComment() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setContent("Great post!");
        savedComment.setCreatedAt(LocalDateTime.now());
        savedComment.setUser(user);

        CommentRequestDTO request = new CommentRequestDTO();
        request.setContent("Great post!");


                Mockito.when(commentService.addComment(
                        Mockito.any(),
                        Mockito.anyLong(),
                        Mockito.anyString()
                )).thenReturn(savedComment);


        mockMvc.perform(post("/api/comments/5")
                        .with(user("testuser"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Great post!"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }
}
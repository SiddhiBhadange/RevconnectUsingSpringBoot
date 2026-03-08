package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.FollowRepository;
import com.secondproj.revconnect.repository.UserRepository;
import com.secondproj.revconnect.security.JwtAuthenticationFilter;
import com.secondproj.revconnect.security.JwtUtil;
import com.secondproj.revconnect.service.FollowService;
import com.secondproj.revconnect.service.NotificationService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FollowController.class)
@AutoConfigureMockMvc
class FollowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FollowService followService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private FollowRepository followRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // FOLLOW USER
    @Test
    @WithMockUser(username = "testuser")
    void testFollowUser() throws Exception {

        User target = new User();
        target.setId(2L);

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(target));

        mockMvc.perform(post("/api/follow/2")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    // UNFOLLOW USER
    @Test
    @WithMockUser(username = "testuser")
    void testUnfollowUser() throws Exception {

        User target = new User();
        target.setId(2L);

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(target));

        mockMvc.perform(delete("/api/follow/2")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    // FOLLOWERS COUNT
    @Test
    @WithMockUser
    void testFollowersCount() throws Exception {

        User user = new User();
        user.setId(2L);

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        Mockito.when(followService.getFollowersCount(user))
                .thenReturn(5L);

        mockMvc.perform(get("/api/follow/2/followers/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    // FOLLOWING COUNT
    @Test
    @WithMockUser
    void testFollowingCount() throws Exception {

        User user = new User();
        user.setId(2L);

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        Mockito.when(followService.getFollowingCount(user))
                .thenReturn(3L);

        mockMvc.perform(get("/api/follow/2/following/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    // CHECK FOLLOW STATUS
    @Test
    @WithMockUser(username = "testuser")
    void testIsFollowing() throws Exception {

        User target = new User();
        target.setId(2L);

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(target));

        Mockito.when(followService.isFollowing(Mockito.any(), Mockito.eq(target)))
                .thenReturn(true);

        mockMvc.perform(get("/api/follow/2/is-following"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    // REMOVE FOLLOWER
    @Test
    @WithMockUser(username = "testuser")
    void testRemoveFollower() throws Exception {

        mockMvc.perform(delete("/api/follow/remove/2")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    // GET FOLLOWERS LIST
    @Test
    @WithMockUser(username = "testuser")
    void testGetFollowers() throws Exception {

        User follower = new User();
        follower.setId(2L);
        follower.setUsername("john");
        follower.setName("John Doe");

        Mockito.when(followRepository.findFollowersByUserId(1L))
                .thenReturn(List.of(follower));

        Mockito.when(followRepository.existsByFollowerAndFollowing(Mockito.any(), Mockito.eq(follower)))
                .thenReturn(true);

        mockMvc.perform(get("/api/follow/followers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("john"));
    }

    // GET FOLLOWING LIST
    @Test
    @WithMockUser(username = "testuser")
    void testGetFollowing() throws Exception {

        User following = new User();
        following.setId(3L);
        following.setUsername("alex");
        following.setName("Alex");

        Mockito.when(followRepository.findFollowingByUserId(1L))
                .thenReturn(List.of(following));

        Mockito.when(followRepository.existsByFollowerAndFollowing(Mockito.any(), Mockito.eq(following)))
                .thenReturn(true);

        mockMvc.perform(get("/api/follow/following/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("alex"));
    }
}
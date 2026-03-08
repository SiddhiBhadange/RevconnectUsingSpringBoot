package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.dto.ConnectionResponseDTO;
import com.secondproj.revconnect.model.Connection;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.UserRepository;
import com.secondproj.revconnect.security.JwtAuthenticationFilter;
import com.secondproj.revconnect.security.JwtUtil;
import com.secondproj.revconnect.service.ConnectionService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConnectionController.class)
@AutoConfigureMockMvc
class ConnectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConnectionService connectionService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // SEND REQUEST
    @Test
    void testSendRequest() throws Exception {

        User sender = new User();
        sender.setId(1L);
        sender.setUsername("testuser");

        User receiver = new User();
        receiver.setId(2L);

        Mockito.when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(sender));

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(receiver));

        mockMvc.perform(post("/api/connections/request/2")
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken("testuser", null)
                        )))
                .andExpect(status().isOk())
                .andExpect(content().string("Connection request sent"));
    }

    // RESPOND REQUEST
    @Test
    void testRespondRequest() throws Exception {

        User user = new User();
        user.setId(2L);
        user.setUsername("receiver");

        Mockito.when(userRepository.findByUsername("receiver"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/connections/respond/5")
                        .param("status", "ACCEPTED")
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken("receiver", null)
                        )))
                .andExpect(status().isOk())
                .andExpect(content().string("Request ACCEPTED"));
    }

    // GET PENDING REQUESTS
    @Test
    void testGetPendingRequests() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Connection connection = new Connection();
        connection.setId(1L);
        connection.setStatus("PENDING");

        User sender = new User();
        sender.setId(2L);
        sender.setUsername("sender");

        connection.setSender(sender);
        connection.setReceiver(user);

        Mockito.when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        Mockito.when(connectionService.getPendingRequests(user))
                .thenReturn(List.of(connection));

        mockMvc.perform(get("/api/connections/pending")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken("testuser", null)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    // GET CONNECTIONS
    @Test
    void testGetConnections() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Connection connection = new Connection();
        connection.setId(1L);
        connection.setStatus("ACCEPTED");

        User other = new User();
        other.setId(2L);
        other.setUsername("friend");

        connection.setSender(user);
        connection.setReceiver(other);

        Mockito.when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        Mockito.when(connectionService.getConnections(user))
                .thenReturn(List.of(connection));

        mockMvc.perform(get("/api/connections")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken("testuser", null)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ACCEPTED"));
    }

    // GET CONNECTION STATUS
    @Test
    void testGetConnectionStatus() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        ConnectionResponseDTO dto = new ConnectionResponseDTO();
        dto.setStatus("PENDING");

        Mockito.when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        Mockito.when(connectionService.getConnectionStatus(user, 2L))
                .thenReturn(dto);

        mockMvc.perform(get("/api/connections/status/2")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken("testuser", null)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    // REMOVE CONNECTION
    @Test
    void testRemoveConnection() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Mockito.when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/connections/remove/2")
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken("testuser", null)
                        )))
                .andExpect(status().isOk())
                .andExpect(content().string("Connection removed"));
    }
}
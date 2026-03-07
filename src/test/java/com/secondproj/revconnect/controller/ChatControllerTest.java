package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.model.Message;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.MessageRepository;
import com.secondproj.revconnect.repository.UserRepository;
import com.secondproj.revconnect.security.JwtAuthenticationFilter;
import com.secondproj.revconnect.security.JwtUtil;
import com.secondproj.revconnect.service.NotificationService;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageRepository messageRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtUtil jwtUtil;

    // =========================
    // TEST SEND MESSAGE
    // =========================
    @Test
    void testSendMessage() throws Exception {

        User sender = new User();
        sender.setId(1L);
        sender.setUsername("testuser");

        User receiver = new User();
        receiver.setId(2L);
        receiver.setUsername("receiver");

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent("Hello");

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(receiver));

        Mockito.when(messageRepository.save(Mockito.any(Message.class)))
                .thenReturn(message);

        mockMvc.perform(post("/api/chat/2")
                        .principal(new UsernamePasswordAuthenticationToken(sender, null))
                        .content("Hello")
                        .contentType("text/plain"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Hello"));
    }

    // =========================
    // TEST GET CHAT
    // =========================
    @Test
    void testGetChat() throws Exception {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("testuser");

        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("receiver");

        Message message = new Message();
        message.setSender(currentUser);
        message.setReceiver(otherUser);
        message.setContent("Hello");

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(otherUser));

        Mockito.when(messageRepository
                        .findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
                                Mockito.any(), Mockito.any(),
                                Mockito.any(), Mockito.any()))
                .thenReturn(List.of(message));

        mockMvc.perform(get("/api/chat/2")
                        .principal(new UsernamePasswordAuthenticationToken(currentUser, null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hello"));
    }
}
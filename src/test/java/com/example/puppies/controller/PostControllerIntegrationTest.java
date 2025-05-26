package com.example.puppies.controller;

import com.example.puppies.dto.AuthRequest;
import com.example.puppies.dto.PostRequest;
import com.example.puppies.dto.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    Long userId;
    String jwtToken; // Added field to store JWT token

    @BeforeEach
    void setup() throws Exception { // Renamed and updated method
        // Create User
        UserRequest ur = new UserRequest("Carlos", "carlos@example.com", "pwd123");
        String userResponseBody = mvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(ur))
        ).andExpect(status().isOk()) // Ensure user creation is successful
         .andReturn().getResponse().getContentAsString();
        userId = mapper.readTree(userResponseBody).get("id").asLong();

        // Login User
        AuthRequest authRequest = new AuthRequest("carlos@example.com", "pwd123");
        String authResponseBody = mvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(authRequest))
        ).andExpect(status().isOk()) // Ensure login is successful
         .andReturn().getResponse().getContentAsString();
        jwtToken = mapper.readTree(authResponseBody).get("token").asText();
    }

    @Test
    void createAndFetchFeed_shouldWork() throws Exception {
        // Crear post
        PostRequest pr = new PostRequest(userId, "http://img.jpg", "Hola puppies");
        mvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + jwtToken) // Added Authorization header
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(pr))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("Hola puppies"));

        // Obtener feed
        mvc.perform(get("/api/posts/feed")
                .header("Authorization", "Bearer " + jwtToken) // Added Authorization header
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].userId").value(userId));
    }
}

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
class LikeControllerIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    Long userId;
    Long postId;
    String jwtToken;

    @BeforeEach
    void setup() throws Exception {
        // Create User
        UserRequest ur = new UserRequest("David", "david@example.com", "pwd123");
        String userResponseBody = mvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(ur))
        ).andExpect(status().isOk()) // Ensure user creation is successful
         .andReturn().getResponse().getContentAsString();
        userId = mapper.readTree(userResponseBody).get("id").asLong();

        // Login User
        AuthRequest authRequest = new AuthRequest("david@example.com", "pwd123");
        String authResponseBody = mvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(authRequest))
        ).andExpect(status().isOk()) // Ensure login is successful
         .andReturn().getResponse().getContentAsString();
        jwtToken = mapper.readTree(authResponseBody).get("token").asText();

        // Create Post
        PostRequest pr = new PostRequest(userId, "http://anotherimg.jpg", "Hello again puppies");
        String postResponseBody = mvc.perform(post("/api/posts")
            .header("Authorization", "Bearer " + jwtToken) // Added Authorization header
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(pr))
        ).andExpect(status().isOk()) // Ensure post creation is successful
         .andReturn().getResponse().getContentAsString();
        postId = mapper.readTree(postResponseBody).get("id").asLong();
    }

    @Test
    void likePost_shouldWork() throws Exception {
        mvc.perform(post("/api/posts/" + postId + "/likes")
                .header("Authorization", "Bearer " + jwtToken)
                .param("userId", String.valueOf(userId)) // Send userId as request parameter
            )
            .andExpect(status().isOk());
            // Cannot reliably check the response body for likePost as it's ResponseEntity<Void>
            // .andExpect(jsonPath("$.userId").value(userId))
            // .andExpect(jsonPath("$.postId").value(postId));
    }
}

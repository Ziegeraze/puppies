package com.example.puppies.controller;

import com.example.puppies.dto.AuthRequest;
import com.example.puppies.dto.PostRequest;
import com.example.puppies.dto.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest { // Renamed from UserIntegrationTest

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private Long userId;
    private Long postId1;
    private Long postId2;
    private String jwtToken; // Added for JWT token
    private static int userCounter = 0; // Add a counter for unique emails

    @BeforeEach
    void setup() throws Exception {
        userCounter++; // Increment counter for unique email
        // Create user
        UserRequest ureq = UserRequest.builder()
            .name("TestUser")
            .email("testuser" + userCounter + "@example.com") // Use unique email
            .password("password")
            .build();
        String ures = mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(ureq)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        userId = mapper.readTree(ures).get("id").asLong();

        // Login user to get JWT token
        AuthRequest authRequest = new AuthRequest("testuser" + userCounter + "@example.com", "password"); // Use unique email
        String authResponseBody = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(authRequest)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        jwtToken = mapper.readTree(authResponseBody).get("token").asText();

        // Create two posts
        PostRequest preq1 = PostRequest.builder()
            .userId(userId)
            .imageUrl("http://image1.jpg")
            .content("First post")
            .build();
        String p1 = mvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + jwtToken) // Added JWT token
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(preq1)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        postId1 = mapper.readTree(p1).get("id").asLong();

        PostRequest preq2 = PostRequest.builder()
            .userId(userId)
            .imageUrl("http://image2.jpg")
            .content("Second post")
            .build();
        String p2 = mvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + jwtToken) // Added JWT token
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(preq2)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        postId2 = mapper.readTree(p2).get("id").asLong();

        // Like first post
        mvc.perform(post("/api/posts/" + postId1 + "/likes")
                .header("Authorization", "Bearer " + jwtToken) // Added JWT token
                .param("userId", userId.toString()))
            .andExpect(status().isOk());
    }

    @Test
    void createUser_shouldReturn200AndUser() throws Exception {
        userCounter++; // Increment counter for unique email
        UserRequest req = UserRequest.builder()
            .name("Juan")
            .email("juan" + userCounter + "@example.com") // Use unique email
            .password("secret123")
            .build();

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value("Juan"))
            .andExpect(jsonPath("$.email").value("juan" + userCounter + "@example.com"));
    }

    @Test
    void shouldGetUserProfile() throws Exception {
        mvc.perform(get("/api/users/" + userId)
                .header("Authorization", "Bearer " + jwtToken)) 
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$.name").value("TestUser"))
            .andExpect(jsonPath("$.email").value("testuser" + userCounter + "@example.com")); // Use unique email
    }

    @Test
    void shouldListUserPosts() throws Exception {
        mvc.perform(get("/api/users/" + userId + "/posts")
                .header("Authorization", "Bearer " + jwtToken)) // Added JWT token for consistency if endpoint becomes secured
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(postId2)) // newest first
            .andExpect(jsonPath("$[1].id").value(postId1));
    }

    @Test
    void shouldListUserLikedPosts() throws Exception {
        mvc.perform(get("/api/users/" + userId + "/likes")
                .header("Authorization", "Bearer " + jwtToken)) // Se añade token JWT por consistencia si el endpoint se asegura
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(postId1));
    }

    // -----------------------------------
    // Pruebas de Manejo de Errores
    // -----------------------------------

    @Test
    void getNonexistentUser_shouldReturn404() throws Exception {
        mvc.perform(get("/api/users/9999")
                .header("Authorization", "Bearer " + jwtToken)) // Se añade token JWT
            .andExpect(status().isNotFound());
    }

    @Test
    void getPostsForNonexistentUser_shouldReturn404() throws Exception {
        mvc.perform(get("/api/users/9999/posts")
                .header("Authorization", "Bearer " + jwtToken)) // Se añade token JWT
            .andExpect(status().isNotFound());
    }

    @Test
    void getLikesForNonexistentUser_shouldReturn404() throws Exception {
        mvc.perform(get("/api/users/9999/likes")
                .header("Authorization", "Bearer " + jwtToken)) // Se añade token JWT
            .andExpect(status().isNotFound());
    }
}

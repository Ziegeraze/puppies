package com.example.puppies.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ValidationErrorIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private String jwtToken; // Added for JWT token
    private static int userCounter = 0;
    private Long testUserId;

    @BeforeEach
    void setup() throws Exception {
        userCounter++;
        // Create a valid user for tests that require an authenticated user
        UserRequest validUserRequest = UserRequest.builder()
            .name("ValidUser" + userCounter)
            .email("validuser" + userCounter + "@example.com")
            .password("password123")
            .build();
        String ures = mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(validUserRequest)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        testUserId = mapper.readTree(ures).get("id").asLong();

        // Login user to get JWT token
        com.example.puppies.dto.AuthRequest authRequest = new com.example.puppies.dto.AuthRequest("validuser" + userCounter + "@example.com", "password123");
        String authResponseBody = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(authRequest)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        jwtToken = mapper.readTree(authResponseBody).get("token").asText();
    }

    @Test
    void createUser_withInvalidEmail_shouldReturn400() throws Exception {
        UserRequest ureq = UserRequest.builder()
            .name("BadEmail")
            .email("not-an-email") // Invalid email format
            .password("pwd123")
            .build();

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(ureq)))
            .andExpect(status().isBadRequest()) // Expect 400 Bad Request
            .andExpect(jsonPath("$.errors.email").exists()); // Expect error message for email field
    }

    @Test
    void createUser_withBlankName_shouldReturn400() throws Exception {
        UserRequest ureq = UserRequest.builder()
            .name("") // Blank name
            .email("blankname@example.com")
            .password("pwd123")
            .build();

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(ureq)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void createPost_withEmptyImageUrl_shouldReturn400() throws Exception {
        PostRequest preq = PostRequest.builder()
            .userId(testUserId) // Use the valid user created in setup
            .imageUrl("") // Empty image URL
            .content("Content without URL")
            .build();

        mvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + jwtToken) // Added JWT token
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(preq)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.imageUrl").exists());
    }

    @Test
    void createPost_withNullContent_shouldReturn400() throws Exception {
        PostRequest preq = PostRequest.builder()
            .userId(testUserId) // Use the valid user created in setup
            .imageUrl("http://valid.url/image.jpg")
            .content(null) // Null content
            .build();

        mvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + jwtToken) // Added JWT token
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(preq)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.content").exists());
    }
}

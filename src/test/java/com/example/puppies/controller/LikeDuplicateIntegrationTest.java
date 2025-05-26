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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Changed to AFTER_EACH_TEST_METHOD
class LikeDuplicateIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private Long userId;
    private Long postId;
    private String jwtToken; // Added for JWT token
    private static int userCounter = 0;

    @BeforeEach
    void setup() throws Exception {
        userCounter++;
        // Create user
        UserRequest ureq = UserRequest.builder()
            .name("DupUser" + userCounter)
            .email("dupuser" + userCounter + "@example.com")
            .password("pwddup")
            .build();
        String ures = mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(ureq)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        userId = mapper.readTree(ures).get("id").asLong();

        // Login user to get JWT token
        com.example.puppies.dto.AuthRequest authRequest = new com.example.puppies.dto.AuthRequest("dupuser" + userCounter + "@example.com", "pwddup");
        String authResponseBody = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(authRequest)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        jwtToken = mapper.readTree(authResponseBody).get("token").asText();

        // Create post
        PostRequest preq = PostRequest.builder()
            .userId(userId)
            .imageUrl("http://dup.jpg")
            .content("Duplicate like test")
            .build();
        String pres = mvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + jwtToken) // Added JWT token
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(preq)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        postId = mapper.readTree(pres).get("id").asLong();
    }

    @Test
    void duplicateLike_shouldBeIdempotent() throws Exception {
        // Primera llamada, like exitoso
        mvc.perform(post("/api/posts/" + postId + "/likes")
                .header("Authorization", "Bearer " + jwtToken) // Added JWT token
                .param("userId", userId.toString()))
            .andExpect(status().isOk());

        // Segunda llamada, debería ser idempotente (200 OK)
        // Nuestra implementacion actual de likePost previene duplicados y retorna.
        // No devuelve un error, simplemente no crea otro like.
        mvc.perform(post("/api/posts/" + postId + "/likes")
                .header("Authorization", "Bearer " + jwtToken) // Added JWT token
                .param("userId", userId.toString()))
            .andExpect(status().isOk());
    }
}

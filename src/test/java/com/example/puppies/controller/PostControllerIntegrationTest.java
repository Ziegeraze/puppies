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

    private String jwtToken;
    private Long userId; // Added to store created user's ID
    private static int userCounter = 0; // Add a counter for unique emails


    @BeforeEach
    void setup() throws Exception {
        userCounter++; // Increment counter for unique email
        // Create user
        UserRequest ureq = UserRequest.builder()
            .name("Carlos")
            .email("carlos" + userCounter + "@example.com") // Use unique email
            .password("password123")
            .build();
        String ures = mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(ureq)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        userId = mapper.readTree(ures).get("id").asLong(); // Store userId

        // Login user to get JWT token
        AuthRequest authRequest = new AuthRequest("carlos" + userCounter + "@example.com", "password123"); // Use unique email
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

    @Test
    void createPost_shouldReturn200AndPost() throws Exception {
        PostRequest req = PostRequest.builder()
            .userId(userId) // Use the userId from setup
            .imageUrl("http://newimage.jpg")
            .content("A new post")
            .build();
        mvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("A new post"));
    }

    @Test
    void likePost_shouldReturn200() throws Exception {
        // Create a post first
        PostRequest postReq = PostRequest.builder()
            .userId(userId) // Use the userId from setup
            .imageUrl("http://likableimage.jpg")
            .content("A post to be liked")
            .build();
        String postRes = mvc.perform(post("/api/posts")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(postReq))
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Long validPostId = mapper.readTree(postRes).get("id").asLong();

        mvc.perform(post("/api/posts/" + validPostId + "/likes")
                .header("Authorization", "Bearer " + jwtToken)
                .param("userId", userId.toString()))
            .andExpect(status().isOk());
    }

    @Test
    void unlikePost_shouldReturn200() throws Exception {
        // Create a post
        PostRequest postReq = PostRequest.builder()
            .userId(userId) // Use the userId from setup
            .imageUrl("http://unlikableimage.jpg")
            .content("A post to be unliked")
            .build();
        String postRes = mvc.perform(post("/api/posts")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(postReq))
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Long validPostId = mapper.readTree(postRes).get("id").asLong();

        // Like the post first
        mvc.perform(post("/api/posts/" + validPostId + "/likes")
                .header("Authorization", "Bearer " + jwtToken)
                .param("userId", userId.toString()))
            .andExpect(status().isOk());

        // Now unlike the post
        mvc.perform(delete("/api/posts/" + validPostId + "/likes")
                .header("Authorization", "Bearer " + jwtToken)
                .param("userId", userId.toString()))
            .andExpect(status().isOk());
    }

    // -----------------------------------
    // Pruebas de Manejo de Errores
    // -----------------------------------

    @Test
    void createPost_withInvalidUser_shouldReturn404() throws Exception {
        PostRequest req = PostRequest.builder()
            .userId(9999L) // Non-existent user ID
            .imageUrl("http://newimage.jpg")
            .content("Content")
            .build();
        mvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + jwtToken) // Se añade token JWT
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isNotFound());
    }

    @Test
    void likePost_nonexistentPost_shouldReturn404() throws Exception {
        mvc.perform(post("/api/posts/9999/likes")
                .header("Authorization", "Bearer " + jwtToken)
                .param("userId", userId.toString())) // Use the userId from setup
            .andExpect(status().isNotFound());
    }

    @Test
    void likePost_withNonexistentUser_shouldReturn404() throws Exception {
        // Create a post first
        PostRequest postReq = PostRequest.builder()
            .userId(userId) // Use the userId from setup
            .imageUrl("http://anotherimage.jpg")
            .content("Another post")
            .build();
        String postRes = mvc.perform(post("/api/posts")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(postReq))
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Long validPostId = mapper.readTree(postRes).get("id").asLong();

        mvc.perform(post("/api/posts/" + validPostId + "/likes")
                .header("Authorization", "Bearer " + jwtToken) // Se añade token JWT
                .param("userId", "9999")) // ID de usuario que no existe
            .andExpect(status().isNotFound());
    }
}

package com.example.puppies.controller;

import com.example.puppies.dto.UserRequest;
import com.example.puppies.dto.UserResponse;
import com.example.puppies.dto.PostResponse; // Added import for PostResponse
import com.example.puppies.model.User;
import com.example.puppies.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/users", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final com.example.puppies.service.PostService postService;
    private final com.example.puppies.service.LikeService likeService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(request.getPassword())
            .build();
        User created = userService.createUser(user);
        UserResponse response = UserResponse.builder()
            .id(created.getId())
            .name(created.getName())
            .email(created.getEmail())
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable Long id) {
        return userService.findById(id)
            .map(u -> UserResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .build())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/posts")
    public ResponseEntity<List<PostResponse>> getUserPosts(@PathVariable Long id) {
        List<PostResponse> posts = postService.getPostsByUser(id).stream()
            .map(p -> PostResponse.builder()
                .id(p.getId())
                .imageUrl(p.getImageUrl())
                .content(p.getContent())
                .createdAt(p.getCreatedAt())
                .userId(p.getUser().getId())
                .build())
            .collect(Collectors.toList());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}/likes")
    public ResponseEntity<List<PostResponse>> getUserLikedPosts(@PathVariable Long id) {
        List<PostResponse> liked = likeService.getLikedPostsByUser(id).stream()
            .map(p -> PostResponse.builder()
                .id(p.getId())
                .imageUrl(p.getImageUrl())
                .content(p.getContent())
                .createdAt(p.getCreatedAt())
                .userId(p.getUser().getId())
                .build())
            .collect(Collectors.toList());
        return ResponseEntity.ok(liked);
    }
}

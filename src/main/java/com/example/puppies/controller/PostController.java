package com.example.puppies.controller;

import com.example.puppies.dto.PostRequest;
import com.example.puppies.dto.PostResponse;
import com.example.puppies.model.Post;
import com.example.puppies.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/posts", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest request) {
        Post post = postService.createPost(request.getUserId(), request.getImageUrl(), request.getContent());
        PostResponse response = PostResponse.builder()
            .id(post.getId())
            .imageUrl(post.getImageUrl())
            .content(post.getContent())
            .createdAt(post.getCreatedAt())
            .userId(post.getUser().getId())
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostResponse>> getFeed() {
        List<PostResponse> responses = postService.getFeed().stream().map(post ->
            PostResponse.builder()
                .id(post.getId())
                .imageUrl(post.getImageUrl())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .userId(post.getUser().getId())
                .build()
        ).toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return postService.getPost(id)
            .map(post -> PostResponse.builder()
                .id(post.getId())
                .imageUrl(post.getImageUrl())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .userId(post.getUser().getId())
                .build()
            )
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}

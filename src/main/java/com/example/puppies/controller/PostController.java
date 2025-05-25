package com.example.puppies.controller;

import com.example.puppies.model.Post;
import com.example.puppies.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestParam Long userId,
            @RequestParam String imageUrl,
            @RequestParam String content) {
        Post post = postService.createPost(userId, imageUrl, content);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<Post>> getFeed() {
        return ResponseEntity.ok(postService.getFeed());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Optional<Post> post = postService.getPost(id);
        return post.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
}

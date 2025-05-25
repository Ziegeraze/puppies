package com.example.puppies.controller;

import com.example.puppies.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<Void> likePost(
            @PathVariable Long postId,
            @RequestParam Long userId) {
        likeService.likePost(userId, postId);
        return ResponseEntity.ok().build();
    }
}

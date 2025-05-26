package com.example.puppies.service;

import com.example.puppies.model.Post; // Added import for Post
import java.util.List;

public interface LikeService {
    void likePost(Long userId, Long postId);
    List<Post> getLikedPostsByUser(Long userId);
}

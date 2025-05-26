package com.example.puppies.service;

import com.example.puppies.model.Post;
import java.util.List;
import java.util.Optional;

public interface PostService {
    Post createPost(Long userId, String imageUrl, String content);
    List<Post> getFeed();
    Optional<Post> getPost(Long postId);
    List<Post> getPostsByUser(Long userId);
}

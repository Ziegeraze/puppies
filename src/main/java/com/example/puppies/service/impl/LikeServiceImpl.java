package com.example.puppies.service.impl;

import com.example.puppies.model.Like;
import com.example.puppies.model.Post;
import com.example.puppies.model.User;
import com.example.puppies.repository.LikeRepository;
import com.example.puppies.repository.PostRepository;
import com.example.puppies.repository.UserRepository;
import com.example.puppies.service.LikeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public void likePost(Long userId, Long postId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        Like like = Like.builder()
            .user(user)
            .post(post)
            .build();
        likeRepository.save(like);
    }
}

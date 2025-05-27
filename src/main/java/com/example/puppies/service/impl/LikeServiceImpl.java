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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public void likePost(Long userId, Long postId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found: " + postId));

        // Avoid duplicate likes (due to the unique constraint in the table)
        boolean already = likeRepository
            .findAllByUserId(userId)
            .stream()
            .anyMatch(l -> l.getPost().getId().equals(postId));
        if (already) {
            return;
        }

        Like like = Like.builder()
            .user(user)
            .post(post)
            .build();
        likeRepository.save(like);
    }

    @Override
    @Transactional
    public void unlikePost(Long userId, Long postId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found: " + postId));
        
        likeRepository.deleteByUserAndPost(user, post);
    }

    @Override
    public List<Post> getLikedPostsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found: " + userId);
        }
        return likeRepository.findAllByUserId(userId)
            .stream()
            .map(Like::getPost)
            .collect(Collectors.toList());
    }
}

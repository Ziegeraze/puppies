package com.example.puppies.service;

import com.example.puppies.model.Like;
import com.example.puppies.model.Post;
import com.example.puppies.model.User;
import com.example.puppies.repository.LikeRepository;
import com.example.puppies.repository.PostRepository;
import com.example.puppies.repository.UserRepository;
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
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));
        Like like = Like.builder()
                .user(user)
                .post(post)
                .build();
        likeRepository.save(like);
    }

    @Override
    public List<Post> getLikedPostsByUser(Long userId) {
        return likeRepository.findAllByUserId(userId).stream()
                .map(Like::getPost)
                .collect(Collectors.toList());
    }
}

package com.example.puppies.service.impl;

import com.example.puppies.model.Post;
import com.example.puppies.model.User;
import com.example.puppies.repository.PostRepository;
import com.example.puppies.repository.UserRepository;
import com.example.puppies.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public Post createPost(Long userId, String imageUrl, String content) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Post post = Post.builder()
            .user(user)
            .imageUrl(imageUrl)
            .content(content)
            .build();
        return postRepository.save(post);
    }

    @Override
    public List<Post> getFeed() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public Optional<Post> getPost(Long postId) {
        return postRepository.findById(postId);
    }
}

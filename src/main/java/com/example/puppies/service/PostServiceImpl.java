package com.example.puppies.service;

import com.example.puppies.model.Post;
import com.example.puppies.model.User;
import com.example.puppies.repository.PostRepository;
import com.example.puppies.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Post createPost(Long userId, String imageUrl, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
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

    @Override
    public List<Post> getPostsByUser(Long userId) {
        return postRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }
}

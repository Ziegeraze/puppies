package com.example.puppies.repository;

import com.example.puppies.model.Post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}

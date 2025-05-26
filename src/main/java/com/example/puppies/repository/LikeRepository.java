package com.example.puppies.repository;

import com.example.puppies.model.Like;
import com.example.puppies.model.User; // Added import for User
import com.example.puppies.model.Post; // Added import for Post
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findAllByUserId(Long userId);
    void deleteByUserAndPost(User user, Post post); // Added method to delete a like by user and post
}

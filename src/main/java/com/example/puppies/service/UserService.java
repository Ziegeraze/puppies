package com.example.puppies.service;

import com.example.puppies.model.User;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> findByEmail(String email);
}

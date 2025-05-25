package com.example.puppies.controller;

import com.example.puppies.dto.UserRequest;
import com.example.puppies.dto.UserResponse;
import com.example.puppies.model.User;
import com.example.puppies.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/users", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(request.getPassword())
            .build();
        User created = userService.createUser(user);
        UserResponse response = UserResponse.builder()
            .id(created.getId())
            .name(created.getName())
            .email(created.getEmail())
            .build();
        return ResponseEntity.ok(response);
    }
}

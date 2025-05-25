package com.example.puppies.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserRequest {
    @NotBlank private String name;
    @Email @NotBlank private String email;
    @NotBlank private String password;
}

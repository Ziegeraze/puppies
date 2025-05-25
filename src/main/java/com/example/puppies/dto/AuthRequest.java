package com.example.puppies.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthRequest {
    @Email @NotBlank private String email;
    @NotBlank private String password;
}

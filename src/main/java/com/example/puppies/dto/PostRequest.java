package com.example.puppies.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PostRequest {
    @NotNull private Long userId;
    @NotBlank private String imageUrl;
    @NotBlank private String content;
}

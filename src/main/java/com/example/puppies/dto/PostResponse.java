package com.example.puppies.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PostResponse {
    private Long id;
    private String imageUrl;
    private String content;
    private LocalDateTime createdAt;
    private Long userId;
}

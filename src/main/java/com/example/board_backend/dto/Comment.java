package com.example.board_backend.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Comment {
    private Long id;
    private Long postId;
    private Long memberId;
    private String content;
    private String author;
    private LocalDateTime createdAt;
}
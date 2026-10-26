package com.example.board_backand.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Comment {
    private Long id;
    private Long postId;
    private String content;
    private String author;
    private LocalDateTime createdAt;
}
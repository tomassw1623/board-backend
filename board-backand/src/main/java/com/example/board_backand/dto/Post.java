package com.example.board_backand.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class Post {
    private Long id;
    private String title;
    private String content;
    private String author;
    private Integer views;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
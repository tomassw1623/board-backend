// PostResponse.java
package com.example.board_backand.dto.response;

import com.example.board_backand.dto.Post;
import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String title,
        String content,
        String author,
        int views,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getViews() == null ? 0 : post.getViews(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
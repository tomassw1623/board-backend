// PostResponse.java
package com.example.board_backend.dto.response;

import com.example.board_backend.dto.Post;
import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        Long memberId,
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
                post.getMemberId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getViews() == null ? 0 : post.getViews(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
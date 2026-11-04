// PostListResponse.java
package com.example.board_backend.dto.response;

import java.util.List;

public record PostListResponse(
        List<PostResponse> posts,
        int totalPages,
        int currentPage
) {}
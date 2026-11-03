// PostListResponse.java
package com.example.board_backand.dto.response;

import java.util.List;

public record PostListResponse(
        List<PostResponse> posts,
        int totalPages,
        int currentPage
) {}
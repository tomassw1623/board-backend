package com.example.board_backand.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
        @NotBlank(message = "작성자를 입력해주세요.")
        @Size(max = 100, message = "작성자는 100자 이하여야 합니다.")
        String author,

        @NotBlank(message = "댓글 내용을 입력해주세요.")
        @Size(max = 1000, message = "댓글은 1,000자 이하여야 합니다.")
        String content
) {}
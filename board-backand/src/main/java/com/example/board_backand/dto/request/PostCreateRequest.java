package com.example.board_backand.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostCreateRequest(
        @NotBlank(message = "제목을 입력해주세요.")
        @Size(max = 255, message = "제목은 255자 이하여야 합니다.")
        String title,

        @NotBlank(message = "내용을 입력해주세요.")
        @Size(max = 10000, message = "내용은 10,000자 이하여야 합니다.")
        String content
) {}
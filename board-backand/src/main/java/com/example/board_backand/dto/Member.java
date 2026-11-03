package com.example.board_backand.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class Member {
    private Long id;
    private String username;
    private String password;   // 암호화된 값. 절대 응답으로 내보내지 않음
    private String nickname;
    private LocalDateTime createdAt;
}
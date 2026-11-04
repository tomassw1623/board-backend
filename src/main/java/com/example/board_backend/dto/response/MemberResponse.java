package com.example.board_backend.dto.response;

import com.example.board_backend.dto.Member;

public record MemberResponse(Long id, String username, String nickname) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getUsername(), member.getNickname());
    }
}
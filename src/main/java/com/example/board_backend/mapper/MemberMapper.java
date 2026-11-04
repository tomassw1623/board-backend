package com.example.board_backend.mapper;

import com.example.board_backend.dto.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    void insert(Member member);
    Member findByUsername(String username);
    Member findById(Long id);
    int countByUsername(String username);
}
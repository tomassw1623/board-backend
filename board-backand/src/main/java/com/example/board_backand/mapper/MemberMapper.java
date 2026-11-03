package com.example.board_backand.mapper;

import com.example.board_backand.dto.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    void insert(Member member);
    Member findByUsername(String username);
    Member findById(Long id);
    int countByUsername(String username);
}
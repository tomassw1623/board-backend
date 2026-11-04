package com.example.board_backend.mapper;

import com.example.board_backend.dto.Comment;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> findByPostId(Long postId);
    Comment findById(Long id);
    void insert(Comment comment);
    int delete(Long id);
}
package com.example.board_backand.mapper;

import com.example.board_backand.dto.Comment;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> findByPostId(Long postId);
    void insert(Comment comment);
    void delete(Long id);
}
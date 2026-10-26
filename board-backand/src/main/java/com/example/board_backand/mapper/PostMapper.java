package com.example.board_backand.mapper;

import com.example.board_backand.dto.Post;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PostMapper {
    List<Post> findAll();
    Post findById(Long id);
    void insertPost(Post post);
    void updatePost(Post post);
    void deletePost(Long id);
}
package com.example.board_backand.mapper;

import com.example.board_backand.dto.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PostMapper {
    List<Post> findAll(@Param("keyword") String keyword, @Param("size") int size, @Param("offset") int offset);
    int countPosts(@Param("keyword") String keyword);
    Post findById(Long id);
    void insertPost(Post post);
    void updatePost(Post post);
    void deletePost(Long id);
}
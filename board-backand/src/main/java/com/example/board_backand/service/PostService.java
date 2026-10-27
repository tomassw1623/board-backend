package com.example.board_backand.service;

import com.example.board_backand.dto.Post;
import com.example.board_backand.mapper.PostMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class PostService {

    private final PostMapper postMapper;

    public PostService(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    public List<Post> getPosts(String keyword, int size, int offset) {
        return postMapper.findAll(keyword, size, offset);
    }

    public int getTotalPages(String keyword, int size) {
        int totalCount = postMapper.countPosts(keyword);
        if (totalCount == 0) {
            return 1; // 글이 없어도 최소 1페이지는 유지
        }
        return (int) Math.ceil((double) totalCount / size);
    }
    
    public Post getPostById(Long id) {
        return postMapper.findById(id);
    }

    public void createPost(Post post) {
        postMapper.insertPost(post);
    }

    public void updatePost(Long id, Post post) {
        post.setId(id);
        postMapper.updatePost(post);
    }

    public void deletePost(Long id) {
        postMapper.deletePost(id);
    }
}
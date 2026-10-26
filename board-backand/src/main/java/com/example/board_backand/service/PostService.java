package com.example.board_backand.service;

import com.example.board_backand.dto.Post;
import com.example.board_backand.mapper.PostMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostService {

    private final PostMapper postMapper;

    public PostService(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    public List<Post> getAllPosts() {
        return postMapper.findAll();
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
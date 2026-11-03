package com.example.board_backand.service;

import com.example.board_backand.dto.Post;
import com.example.board_backand.dto.request.PostCreateRequest;
import com.example.board_backand.dto.request.PostUpdateRequest;
import com.example.board_backand.dto.response.PostResponse;
import com.example.board_backand.exception.NotFoundException;
import com.example.board_backand.mapper.PostMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    private final PostMapper postMapper;

    public PostService(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    public List<PostResponse> getPosts(String keyword, int size, int offset) {
        return postMapper.findAll(keyword, size, offset).stream()
                .map(PostResponse::from)
                .toList();
    }

    public int getTotalPages(String keyword, int size) {
        int totalCount = postMapper.countPosts(keyword);
        if (totalCount == 0) {
            return 1;
        }
        return (int) Math.ceil((double) totalCount / size);
    }
    
    @Transactional
    public PostResponse getPostAndIncreaseViews(Long id) {
        if (postMapper.increaseViews(id) == 0) {
            throw new NotFoundException("게시글을 찾을 수 없습니다. id=" + id);
        }
        return PostResponse.from(postMapper.findById(id));
    }

    public void createPost(PostCreateRequest request) {
        Post post = new Post();
        post.setTitle(request.title().trim());
        post.setContent(request.content());
        post.setAuthor(request.author().trim());
        postMapper.insertPost(post);
    }

    @Transactional
    public PostResponse updatePost(Long id, PostUpdateRequest request) {
        Post post = new Post();
        post.setId(id);
        post.setTitle(request.title().trim());
        post.setContent(request.content());

        if (postMapper.updatePost(post) == 0) {
            throw new NotFoundException("게시글을 찾을 수 없습니다. id=" + id);
        }
        return PostResponse.from(postMapper.findById(id));
    }

    public void deletePost(Long id) {
        if (postMapper.deletePost(id) == 0) {
            throw new NotFoundException("게시글을 찾을 수 없습니다. id=" + id);
        }
    }
}
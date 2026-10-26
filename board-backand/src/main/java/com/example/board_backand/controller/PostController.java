package com.example.board_backand.controller;

import com.example.board_backand.dto.Post;
import com.example.board_backand.service.PostService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:5173") // React 개발 서버 연동용
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 전체 목록 조회 (Read)
    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    // 상세 조회 (Read)
    @GetMapping("/{id}")
    public Post getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    // 글 작성 (Create)
    @PostMapping
    public void createPost(@RequestBody Post post) {
        postService.createPost(post);
    }

    // 글 수정 (Update)
    @PutMapping("/{id}")
    public void updatePost(@PathVariable Long id, @RequestBody Post post) {
        postService.updatePost(id, post);
    }

    // 글 삭제 (Delete)
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }
}
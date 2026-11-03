package com.example.board_backand.controller;

import com.example.board_backand.dto.request.PostCreateRequest;       // ★
import com.example.board_backand.dto.request.PostUpdateRequest;       // ★
import com.example.board_backand.dto.response.PostListResponse;       // ★
import com.example.board_backand.dto.response.PostResponse;           // ★
import com.example.board_backand.service.PostService;
import jakarta.validation.Valid;                                      // ★
import org.springframework.http.HttpStatus;                           // ★
import org.springframework.web.bind.annotation.*;

import java.util.List;
// ★ java.util.HashMap, java.util.Map import 는 삭제

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:5173") // React 개발 서버 연동용
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // ★ 반환 타입 Map<String, Object> → PostListResponse
    @GetMapping
    public PostListResponse getPosts(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        page = Math.max(page, 1);                       // ★ 추가
        size = Math.min(Math.max(size, 1), 100);        // ★ 추가

        int offset = (page - 1) * size;
        List<PostResponse> posts = postService.getPosts(keyword, size, offset);
        int totalPages = postService.getTotalPages(keyword, size);

        return new PostListResponse(posts, totalPages, page);   // ★ Map 만들던 부분 교체
    }

    // ★ 반환 PostResponse, 서비스 메서드 이름 변경
    @GetMapping("/{id}")
    public PostResponse getPostById(@PathVariable Long id) {
        return postService.getPostAndIncreaseViews(id);
    }

    // ★ @Valid, DTO 변경, 201 응답
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPost(@Valid @RequestBody PostCreateRequest request) {
        postService.createPost(request);
    }

    // ★ @Valid, DTO 변경, 반환 PostResponse
    @PutMapping("/{id}")
    public PostResponse updatePost(@PathVariable Long id,
                                   @Valid @RequestBody PostUpdateRequest request) {
        return postService.updatePost(id, request);
    }

    // ★ 204 응답
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }
}
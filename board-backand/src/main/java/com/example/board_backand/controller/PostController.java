package com.example.board_backand.controller;

import com.example.board_backand.config.AuthSupport;
import com.example.board_backand.dto.Member;
import com.example.board_backand.dto.request.PostCreateRequest;
import com.example.board_backand.dto.request.PostUpdateRequest;
import com.example.board_backand.dto.response.PostListResponse;
import com.example.board_backand.dto.response.PostResponse;
import com.example.board_backand.service.MemberService;
import com.example.board_backand.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final MemberService memberService;

    public PostController(PostService postService, MemberService memberService) {
        this.postService = postService;
        this.memberService = memberService;
    }

    @GetMapping
    public PostListResponse getPosts(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        page = Math.max(page, 1);
        size = Math.min(Math.max(size, 1), 100);
        int offset = (page - 1) * size;

        List<PostResponse> posts = postService.getPosts(keyword, size, offset);
        int totalPages = postService.getTotalPages(keyword, size);

        return new PostListResponse(posts, totalPages, page);
    }

    @GetMapping("/{id}")
    public PostResponse getPostById(@PathVariable Long id) {
        return postService.getPostAndIncreaseViews(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPost(@Valid @RequestBody PostCreateRequest request,
                           HttpServletRequest httpRequest) {
        Member member = memberService.getMember(AuthSupport.requireLoginId(httpRequest));
        postService.createPost(member, request);
    }

    @PutMapping("/{id}")
    public PostResponse updatePost(@PathVariable Long id,
                                   @Valid @RequestBody PostUpdateRequest request,
                                   HttpServletRequest httpRequest) {
        Long loginMemberId = AuthSupport.requireLoginId(httpRequest);
        return postService.updatePost(id, loginMemberId, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long loginMemberId = AuthSupport.requireLoginId(httpRequest);
        postService.deletePost(id, loginMemberId);
    }
}
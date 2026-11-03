package com.example.board_backand.controller;

import com.example.board_backand.config.AuthSupport;
import com.example.board_backand.dto.Comment;
import com.example.board_backand.dto.Member;
import com.example.board_backand.dto.request.CommentCreateRequest;
import com.example.board_backand.service.CommentService;
import com.example.board_backand.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    private final MemberService memberService;

    public CommentController(CommentService commentService, MemberService memberService) {
        this.commentService = commentService;
        this.memberService = memberService;
    }

    @GetMapping("/posts/{postId}/comments")
    public List<Comment> getComments(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public void addComment(@PathVariable Long postId,
                           @Valid @RequestBody CommentCreateRequest request,
                           HttpServletRequest httpRequest) {
        Member member = memberService.getMember(AuthSupport.requireLoginId(httpRequest));
        commentService.addComment(postId, member, request);
    }

    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long loginMemberId = AuthSupport.requireLoginId(httpRequest);
        commentService.deleteComment(id, loginMemberId);
    }
}
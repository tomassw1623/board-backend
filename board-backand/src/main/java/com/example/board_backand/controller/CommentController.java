package com.example.board_backand.controller;

import com.example.board_backand.dto.Comment;
import com.example.board_backand.dto.request.CommentCreateRequest;
import com.example.board_backand.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/posts/{postId}/comments")
    public List<Comment> getComments(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public void addComment(@PathVariable Long postId,
                           @Valid @RequestBody CommentCreateRequest request) {
        commentService.addComment(postId, request);
    }

    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}
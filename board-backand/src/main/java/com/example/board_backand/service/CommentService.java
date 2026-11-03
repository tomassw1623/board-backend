package com.example.board_backand.service;

import com.example.board_backand.dto.Comment;
import com.example.board_backand.dto.request.CommentCreateRequest;
import com.example.board_backand.exception.NotFoundException;
import com.example.board_backand.mapper.CommentMapper;
import com.example.board_backand.mapper.PostMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;

    public CommentService(CommentMapper commentMapper, PostMapper postMapper) {
        this.commentMapper = commentMapper;
        this.postMapper = postMapper;
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        validatePostExists(postId);
        return commentMapper.findByPostId(postId);
    }

    public void addComment(Long postId, CommentCreateRequest request) {
        validatePostExists(postId);
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthor(request.author().trim());
        comment.setContent(request.content());
        commentMapper.insert(comment);
    }

    public void deleteComment(Long id) {
        if (commentMapper.delete(id) == 0) {
            throw new NotFoundException("댓글을 찾을 수 없습니다. id=" + id);
        }
    }

    private void validatePostExists(Long postId) {
        if (postMapper.findById(postId) == null) {
            throw new NotFoundException("게시글을 찾을 수 없습니다. id=" + postId);
        }
    }
}
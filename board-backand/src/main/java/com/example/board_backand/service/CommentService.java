package com.example.board_backand.service;

import com.example.board_backand.dto.Comment;
import com.example.board_backand.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentMapper.findByPostId(postId);
    }

    public void addComment(Comment comment) {
        commentMapper.insert(comment);
    }

    public void deleteComment(Long id) {
        commentMapper.delete(id);
    }
}
package com.example.board_backand.service;

import com.example.board_backand.dto.Comment;
import com.example.board_backand.dto.Member;
import com.example.board_backand.dto.request.CommentCreateRequest;
import com.example.board_backand.exception.ForbiddenException;
import com.example.board_backand.exception.NotFoundException;
import com.example.board_backand.mapper.CommentMapper;
import com.example.board_backand.mapper.PostMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

    public void addComment(Long postId, Member member, CommentCreateRequest request) {
        validatePostExists(postId);
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setMemberId(member.getId());
        comment.setAuthor(member.getNickname());
        comment.setContent(request.content());
        commentMapper.insert(comment);
    }

    public void deleteComment(Long id, Long loginMemberId) {
        Comment comment = commentMapper.findById(id);
        if (comment == null) {
            throw new NotFoundException("댓글을 찾을 수 없습니다. id=" + id);
        }
        if (!Objects.equals(comment.getMemberId(), loginMemberId)) {
            throw new ForbiddenException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }
        commentMapper.delete(id);
    }

    private void validatePostExists(Long postId) {
        if (postMapper.findById(postId) == null) {
            throw new NotFoundException("게시글을 찾을 수 없습니다. id=" + postId);
        }
    }
}
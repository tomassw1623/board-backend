package com.example.board_backand.service;

import com.example.board_backand.dto.Comment;
import com.example.board_backand.dto.Member;
import com.example.board_backand.dto.Post;
import com.example.board_backand.dto.request.CommentCreateRequest;
import com.example.board_backand.exception.ForbiddenException;
import com.example.board_backand.exception.NotFoundException;
import com.example.board_backand.mapper.CommentMapper;
import com.example.board_backand.mapper.PostMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private CommentService commentService;

    private Comment commentOwnedBy(Long memberId) {
        Comment comment = new Comment();
        comment.setId(20L);
        comment.setMemberId(memberId);
        return comment;
    }

    @Test
    @DisplayName("댓글 작성: 작성자는 로그인한 회원 정보로 저장된다")
    void addComment_usesLoginMember() {
        when(postMapper.findById(10L)).thenReturn(new Post());
        Member member = new Member();
        member.setId(1L);
        member.setNickname("tester");

        commentService.addComment(10L, member, new CommentCreateRequest("댓글"));

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentMapper).insert(captor.capture());
        assertThat(captor.getValue().getPostId()).isEqualTo(10L);
        assertThat(captor.getValue().getMemberId()).isEqualTo(1L);
        assertThat(captor.getValue().getAuthor()).isEqualTo("tester");
    }

    @Test
    @DisplayName("댓글 작성: 없는 글에는 NotFoundException, 저장하지 않는다")
    void addComment_postNotFound() {
        when(postMapper.findById(99L)).thenReturn(null);
        Member member = new Member();
        member.setId(1L);

        assertThatThrownBy(() ->
                commentService.addComment(99L, member, new CommentCreateRequest("댓글")))
                .isInstanceOf(NotFoundException.class);

        verify(commentMapper, never()).insert(any());
    }

    @Test
    @DisplayName("댓글 조회: 없는 글이면 NotFoundException")
    void getComments_postNotFound() {
        when(postMapper.findById(99L)).thenReturn(null);

        assertThatThrownBy(() -> commentService.getCommentsByPostId(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("댓글 삭제: 없는 댓글이면 NotFoundException")
    void deleteComment_notFound() {
        when(commentMapper.findById(20L)).thenReturn(null);

        assertThatThrownBy(() -> commentService.deleteComment(20L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("댓글 삭제: 남의 댓글이면 ForbiddenException, 삭제하지 않는다")
    void deleteComment_forbidden() {
        when(commentMapper.findById(20L)).thenReturn(commentOwnedBy(1L));

        assertThatThrownBy(() -> commentService.deleteComment(20L, 2L))
                .isInstanceOf(ForbiddenException.class);

        verify(commentMapper, never()).delete(any());
    }

    @Test
    @DisplayName("댓글 삭제: 내 댓글이면 삭제된다")
    void deleteComment_owner() {
        when(commentMapper.findById(20L)).thenReturn(commentOwnedBy(1L));

        commentService.deleteComment(20L, 1L);

        verify(commentMapper).delete(20L);
    }
}
package com.example.board_backend.service;

import com.example.board_backend.dto.Member;
import com.example.board_backend.dto.Post;
import com.example.board_backend.dto.request.PostCreateRequest;
import com.example.board_backend.dto.request.PostUpdateRequest;
import com.example.board_backend.dto.response.PostResponse;
import com.example.board_backend.exception.ForbiddenException;
import com.example.board_backend.exception.NotFoundException;
import com.example.board_backend.mapper.PostMapper;
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
class PostServiceTest {

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private Post postOwnedBy(Long memberId) {
        Post post = new Post();
        post.setId(10L);
        post.setMemberId(memberId);
        post.setTitle("제목");
        post.setContent("내용");
        post.setAuthor("작성자");
        post.setViews(0);
        return post;
    }

    @Test
    @DisplayName("글 작성: 작성자는 요청이 아니라 로그인한 회원 정보로 저장된다")
    void createPost_usesLoginMember() {
        Member member = new Member();
        member.setId(1L);
        member.setNickname("tester");

        postService.createPost(member, new PostCreateRequest("  제목  ", "내용"));

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postMapper).insertPost(captor.capture());
        Post saved = captor.getValue();

        assertThat(saved.getMemberId()).isEqualTo(1L);
        assertThat(saved.getAuthor()).isEqualTo("tester");
        assertThat(saved.getTitle()).isEqualTo("제목");   // 앞뒤 공백 제거
    }

    @Test
    @DisplayName("상세 조회: 존재하지 않는 글이면 NotFoundException")
    void getPost_notFound() {
        when(postMapper.increaseViews(99L)).thenReturn(0);

        assertThatThrownBy(() -> postService.getPostAndIncreaseViews(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("상세 조회: 조회수를 올린 뒤 최신 글을 반환한다")
    void getPost_increasesViews() {
        Post post = postOwnedBy(1L);
        post.setViews(1);
        when(postMapper.increaseViews(10L)).thenReturn(1);
        when(postMapper.findById(10L)).thenReturn(post);

        PostResponse response = postService.getPostAndIncreaseViews(10L);

        verify(postMapper).increaseViews(10L);
        assertThat(response.views()).isEqualTo(1);
    }

    @Test
    @DisplayName("수정: 남의 글이면 ForbiddenException, 수정 쿼리는 실행되지 않는다")
    void updatePost_forbidden() {
        when(postMapper.findById(10L)).thenReturn(postOwnedBy(1L));

        assertThatThrownBy(() ->
                postService.updatePost(10L, 2L, new PostUpdateRequest("해킹", "해킹")))
                .isInstanceOf(ForbiddenException.class);

        verify(postMapper, never()).updatePost(any());
    }

    @Test
    @DisplayName("수정: 존재하지 않는 글이면 NotFoundException")
    void updatePost_notFound() {
        when(postMapper.findById(10L)).thenReturn(null);

        assertThatThrownBy(() ->
                postService.updatePost(10L, 1L, new PostUpdateRequest("제목", "내용")))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("수정: 내 글이면 수정된다")
    void updatePost_owner() {
        when(postMapper.findById(10L)).thenReturn(postOwnedBy(1L));

        postService.updatePost(10L, 1L, new PostUpdateRequest("새 제목", "새 내용"));

        verify(postMapper).updatePost(any(Post.class));
    }

    @Test
    @DisplayName("삭제: 남의 글이면 ForbiddenException, 삭제 쿼리는 실행되지 않는다")
    void deletePost_forbidden() {
        when(postMapper.findById(10L)).thenReturn(postOwnedBy(1L));

        assertThatThrownBy(() -> postService.deletePost(10L, 2L))
                .isInstanceOf(ForbiddenException.class);

        verify(postMapper, never()).deletePost(any());
    }

    @Test
    @DisplayName("삭제: 내 글이면 삭제된다")
    void deletePost_owner() {
        when(postMapper.findById(10L)).thenReturn(postOwnedBy(1L));

        postService.deletePost(10L, 1L);

        verify(postMapper).deletePost(10L);
    }

    @Test
    @DisplayName("전체 페이지 수: 글이 없어도 최소 1페이지")
    void totalPages_emptyIsOne() {
        when(postMapper.countPosts("")).thenReturn(0);

        assertThat(postService.getTotalPages("", 10)).isEqualTo(1);
    }

    @Test
    @DisplayName("전체 페이지 수: 올림 계산 (25건, 10개씩 → 3페이지)")
    void totalPages_roundsUp() {
        when(postMapper.countPosts("")).thenReturn(25);

        assertThat(postService.getTotalPages("", 10)).isEqualTo(3);
    }
}
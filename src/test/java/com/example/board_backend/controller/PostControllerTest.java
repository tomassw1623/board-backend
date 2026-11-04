package com.example.board_backend.controller;

import com.example.board_backend.config.SessionConst;
import com.example.board_backend.dto.Member;
import com.example.board_backend.dto.response.PostListResponse;
import com.example.board_backend.dto.response.PostResponse;
import com.example.board_backend.exception.ForbiddenException;
import com.example.board_backend.exception.GlobalExceptionHandler;
import com.example.board_backend.exception.NotFoundException;
import com.example.board_backend.service.MemberService;
import com.example.board_backend.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private MemberService memberService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PostController(postService, memberService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // 로그인한 상태를 흉내 내는 세션
    private MockHttpSession loginSession(Long memberId) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER_ID, memberId);
        return session;
    }

    @Test
    @DisplayName("글 목록: 200과 posts, totalPages를 반환한다")
    void getPosts_ok() throws Exception {
        when(postService.getPosts("", 10, 0)).thenReturn(List.of());
        when(postService.getTotalPages("", 10)).thenReturn(1);

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(1));
    }

    @Test
    @DisplayName("글 상세: 없는 글이면 404")
    void getPost_notFound() throws Exception {
        when(postService.getPostAndIncreaseViews(99L))
                .thenThrow(new NotFoundException("게시글을 찾을 수 없습니다. id=99"));

        mockMvc.perform(get("/api/posts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("글 상세: id에 숫자가 아닌 값이 오면 400")
    void getPost_badId() throws Exception {
        mockMvc.perform(get("/api/posts/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("글 작성: 로그인하지 않으면 401")
    void createPost_unauthorized() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"제목\",\"content\":\"내용\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("글 작성: 제목이 비어 있으면 400과 필드별 메시지")
    void createPost_blankTitle() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .session(loginSession(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"content\":\"내용\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists());
    }

    @Test
    @DisplayName("글 작성: 로그인하고 올바르게 요청하면 201")
    void createPost_created() throws Exception {
        Member member = new Member();
        member.setId(1L);
        member.setNickname("tester");
        when(memberService.getMember(1L)).thenReturn(member);

        mockMvc.perform(post("/api/posts")
                        .session(loginSession(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"제목\",\"content\":\"내용\"}"))
                .andExpect(status().isCreated());

        verify(postService).createPost(eq(member), any());
    }

    @Test
    @DisplayName("글 삭제: 로그인하지 않으면 401")
    void deletePost_unauthorized() throws Exception {
        mockMvc.perform(delete("/api/posts/7"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("글 삭제: 남의 글이면 403")
    void deletePost_forbidden() throws Exception {
        doThrow(new ForbiddenException("본인이 작성한 글만 수정/삭제할 수 있습니다."))
                .when(postService).deletePost(7L, 2L);

        mockMvc.perform(delete("/api/posts/7").session(loginSession(2L)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("글 삭제: 내 글이면 204")
    void deletePost_noContent() throws Exception {
        mockMvc.perform(delete("/api/posts/7").session(loginSession(1L)))
                .andExpect(status().isNoContent());

        verify(postService).deletePost(7L, 1L);
    }
}
package com.example.board_backend.service;

import com.example.board_backend.dto.Member;
import com.example.board_backend.dto.request.LoginRequest;
import com.example.board_backend.dto.request.SignupRequest;
import com.example.board_backend.exception.DuplicateException;
import com.example.board_backend.exception.UnauthorizedException;
import com.example.board_backend.mapper.MemberMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원가입: 비밀번호는 암호화되어 저장된다")
    void signup_encodesPassword() {
        when(memberMapper.countByUsername("tester01")).thenReturn(0);
        when(passwordEncoder.encode("password123")).thenReturn("ENCODED");

        memberService.signup(new SignupRequest("tester01", "password123", "tester"));

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberMapper).insert(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("ENCODED");
        assertThat(captor.getValue().getPassword()).isNotEqualTo("password123");
    }

    @Test
    @DisplayName("회원가입: 이미 있는 아이디면 DuplicateException, 저장하지 않는다")
    void signup_duplicate() {
        when(memberMapper.countByUsername("tester01")).thenReturn(1);

        assertThatThrownBy(() ->
                memberService.signup(new SignupRequest("tester01", "password123", "tester")))
                .isInstanceOf(DuplicateException.class);

        verify(memberMapper, never()).insert(any());
    }

    @Test
    @DisplayName("회원가입: 동시 가입으로 DB UNIQUE에 걸려도 DuplicateException으로 변환된다")
    void signup_duplicateKeyFromDb() {
        when(memberMapper.countByUsername("tester01")).thenReturn(0);
        when(passwordEncoder.encode("password123")).thenReturn("ENCODED");
        doThrow(new DuplicateKeyException("dup")).when(memberMapper).insert(any(Member.class));

        assertThatThrownBy(() ->
                memberService.signup(new SignupRequest("tester01", "password123", "tester")))
                .isInstanceOf(DuplicateException.class);
    }

    @Test
    @DisplayName("로그인: 아이디와 비밀번호가 맞으면 회원을 반환한다")
    void login_success() {
        Member member = new Member();
        member.setId(1L);
        member.setPassword("ENCODED");
        when(memberMapper.findByUsername("tester01")).thenReturn(member);
        when(passwordEncoder.matches("password123", "ENCODED")).thenReturn(true);

        Member result = memberService.login(new LoginRequest("tester01", "password123"));

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("로그인: 비밀번호가 틀리면 UnauthorizedException")
    void login_wrongPassword() {
        Member member = new Member();
        member.setPassword("ENCODED");
        when(memberMapper.findByUsername("tester01")).thenReturn(member);
        when(passwordEncoder.matches("wrong", "ENCODED")).thenReturn(false);

        assertThatThrownBy(() -> memberService.login(new LoginRequest("tester01", "wrong")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("로그인: 없는 아이디도 같은 메시지의 UnauthorizedException (아이디 존재 여부를 숨김)")
    void login_unknownUser_sameMessage() {
        Member member = new Member();
        member.setPassword("ENCODED");
        when(memberMapper.findByUsername("tester01")).thenReturn(member);
        when(memberMapper.findByUsername("nobody")).thenReturn(null);
        when(passwordEncoder.matches("wrong", "ENCODED")).thenReturn(false);

        String wrongPasswordMessage = catchMessage(() ->
                memberService.login(new LoginRequest("tester01", "wrong")));
        String unknownUserMessage = catchMessage(() ->
                memberService.login(new LoginRequest("nobody", "whatever")));

        assertThat(unknownUserMessage).isEqualTo(wrongPasswordMessage);
    }

    private String catchMessage(Runnable action) {
        try {
            action.run();
        } catch (UnauthorizedException e) {
            return e.getMessage();
        }
        throw new AssertionError("UnauthorizedException 이 발생해야 합니다");
    }
}
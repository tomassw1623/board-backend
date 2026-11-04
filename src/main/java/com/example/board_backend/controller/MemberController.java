package com.example.board_backend.controller;

import com.example.board_backend.config.SessionConst;
import com.example.board_backend.dto.Member;
import com.example.board_backend.dto.request.LoginRequest;
import com.example.board_backend.dto.request.SignupRequest;
import com.example.board_backend.dto.response.MemberResponse;
import com.example.board_backend.exception.UnauthorizedException;
import com.example.board_backend.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@Valid @RequestBody SignupRequest request) {
        memberService.signup(request);
    }

    @PostMapping("/login")
    public MemberResponse login(@Valid @RequestBody LoginRequest request,
                                HttpServletRequest httpRequest) {
        Member member = memberService.login(request);

        // 기존 세션을 버리고 새로 만든다 (세션 고정 공격 방지)
        HttpSession old = httpRequest.getSession(false);
        if (old != null) {
            old.invalidate();
        }
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(SessionConst.LOGIN_MEMBER_ID, member.getId());

        return MemberResponse.from(member);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    @GetMapping("/me")
    public MemberResponse me(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);   // 없으면 새로 만들지 않음
        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER_ID) == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Long memberId = (Long) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);
        return MemberResponse.from(memberService.getMember(memberId));
    }
}
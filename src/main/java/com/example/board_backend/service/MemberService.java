package com.example.board_backend.service;

import com.example.board_backend.dto.Member;
import com.example.board_backend.dto.request.LoginRequest;
import com.example.board_backend.dto.request.SignupRequest;
import com.example.board_backend.exception.DuplicateException;
import com.example.board_backend.exception.UnauthorizedException;
import com.example.board_backend.mapper.MemberMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberMapper memberMapper, PasswordEncoder passwordEncoder) {
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public void signup(SignupRequest request) {
        String username = request.username().trim();

        if (memberMapper.countByUsername(username) > 0) {
            throw new DuplicateException("이미 사용 중인 아이디입니다.");
        }

        Member member = new Member();
        member.setUsername(username);
        member.setPassword(passwordEncoder.encode(request.password()));
        member.setNickname(request.nickname().trim());

        try {
            memberMapper.insert(member);
        } catch (DuplicateKeyException e) {
            throw new DuplicateException("이미 사용 중인 아이디입니다.");
        }
    }

    public Member login(LoginRequest request) {
        Member member = memberMapper.findByUsername(request.username().trim());

        // 아이디가 없는 경우와 비밀번호가 틀린 경우를 같은 메시지로 응답한다
        // (어느 쪽이 틀렸는지 알려주면 존재하는 아이디를 알아내는 데 악용될 수 있음)
        if (member == null || !passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new UnauthorizedException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        return member;
    }

    public Member getMember(Long id) {
        Member member = memberMapper.findById(id);
        if (member == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        return member;
    }
}
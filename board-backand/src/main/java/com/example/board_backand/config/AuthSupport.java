package com.example.board_backand.config;

import com.example.board_backand.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class AuthSupport {

    private AuthSupport() {}

    // 로그인하지 않았으면 401 예외를 던진다
    public static Long requireLoginId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null
                && session.getAttribute(SessionConst.LOGIN_MEMBER_ID) instanceof Long id) {
            return id;
        }
        throw new UnauthorizedException("로그인이 필요합니다.");
    }
}
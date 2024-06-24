package com.example.demo.api.user.controller;

import com.example.demo.api.exception.auth.AuthErrorCode;
import com.example.demo.api.exception.auth.AuthException;
import com.example.demo.api.response.ResponseDto;
import com.example.demo.api.user.dto.response.ReissueDto;
import com.example.demo.api.user.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseDto<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        String refresh = Arrays.stream(cookies)
                .filter(cookie -> "refresh".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(
                        () -> new AuthException(AuthErrorCode.NOT_EXISTS_REFRESH_TOKEN)
                );
        ReissueDto reissue = authService.reissue(refresh);

        response.setHeader("access","Bearer " + reissue.getAccessToken());
        response.addCookie(createCookie("refresh",reissue.getRefreshToken()));

        return ResponseDto.success();
    }

    @PostMapping("/logout")
    public ResponseDto<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            throw new AuthException(AuthErrorCode.NOT_EXISTS_COOKIE);
        }

        Cookie refreshCookie = Arrays.stream(cookies)
                .filter(cookie -> "refresh".equals(cookie.getName()))
                .findFirst().orElseThrow(
                        () -> new AuthException(AuthErrorCode.NOT_EXISTS_COOKIE)
                );

        String refresh = refreshCookie.getValue();
        if(refresh == null || refresh.isEmpty()) {
            throw new AuthException(AuthErrorCode.NOT_EXISTS_REFRESH_TOKEN);
        }

        authService.logout(refresh);
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        return ResponseDto.success();
    }


    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);     // 쿠키가 살아있을 시간
        /*cookie.setSecure();*/         // https에서만 동작할것인지 (로컬은 http 환경이라 안먹음)
        /*cookie.setPath("/");*/        // 쿠키가 전역에서 동작
        cookie.setHttpOnly(true);       // http에서만 쿠키가 동작할 수 있도록 (js와 같은곳에서 가져갈 수 없도록)

        return cookie;
    }


}

package com.example.demo.api.user.service;

import com.example.demo.api.exception.auth.AuthErrorCode;
import com.example.demo.api.exception.auth.AuthException;
import com.example.demo.global.config.util.JwtUtil;
import com.example.demo.global.redis.service.RedisService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;


    @Value("${jwt.access-expired-time}")
    Long accessExpiredTime;

    @Value("${jwt.refresh-expired-time}")
    Long refreshExpiredTime;

    public HttpServletResponse reissue(HttpServletRequest request, HttpServletResponse response){


        String refresh = null;
        Cookie[] cookies = request.getCookies();

        for(Cookie cookie : cookies){
            if("refresh".equals(cookie.getName())){
                refresh = cookie.getValue();
            }
        }

        if(refresh == null) {
            throw new AuthException(AuthErrorCode.EXPIRED_REFRESH_TOKEN);
        }
        // 유효기간 확인
        try {
            if(jwtUtil.isExpired(refresh)) {
                throw new AuthException(AuthErrorCode.EXPIRED_REFRESH_TOKEN);
            }
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        String category = jwtUtil.getCategory(refresh);

        if(!category.equals("refresh")){
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);


        String redisRefreshToken = redisService.getValues(username);
        if(redisService.checkExistsValue(redisRefreshToken)){
            throw new AuthException(AuthErrorCode.NOT_EXISTS_REDIS_REFRESH_TOKEN);
        }

        String newAccessToken = jwtUtil.creatAccessToken(username,role,accessExpiredTime);
        String newRefreshToken = jwtUtil.creatRefreshToken(username,role,refreshExpiredTime);

        redisService.setValues(username, newRefreshToken, Duration.ofMillis(refreshExpiredTime));


        response.setHeader("access","Bearer " + newAccessToken);
        response.addCookie(createCookie("refresh",newRefreshToken));


        return response;
    }


    public void logout(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Optional<Cookie> refreshCookie = Arrays.stream(cookies)
                .filter(cookie -> "refresh".equals(cookie.getName()))
                .findFirst();

        if(refreshCookie.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String refreshToken = refreshCookie.get().getValue();
        if(refreshToken == null || refreshToken.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String key = jwtUtil.getUsername(refreshToken);

        if(redisService.getValues(key) == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        redisService.deleteValues(key);

        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.setStatus(HttpServletResponse.SC_OK);
        response.addCookie(cookie);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);     // 쿠키가 살아있을 시간
        /*cookie.setSecure();*/         // https에서만 동작할것인지 (로컬은 http 환경이라 안먹음)
        /*cookie.setPath("/");*/        // 쿠키가 전역에서 동작
        cookie.setHttpOnly(true);       // http에서만 쿠키가 동작할 수 있도록 (js와 같은곳에서 가져갈 수 없도록)

        return cookie;
    }

}

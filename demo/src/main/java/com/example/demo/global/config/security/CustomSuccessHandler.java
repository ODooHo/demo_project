package com.example.demo.global.config.security;

import com.example.demo.global.config.util.JwtUtil;
import com.example.demo.global.auth.oauth.CustomOAuth2User;
import com.example.demo.global.redis.service.RedisService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Value("${jwt.access-expired-time}")
    Long accessExpiredTime;

    @Value("${jwt.refresh-expired-time}")
    Long refreshExpiredTime;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customUserDetail = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetail.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String accessToken = jwtUtil.creatAccessToken(username,role,accessExpiredTime);
        String refreshToken = jwtUtil.creatRefreshToken(username,role,refreshExpiredTime);

        redisService.setValues(username, refreshToken, Duration.ofMillis(refreshExpiredTime));

        response.setHeader("access", "Bearer " + accessToken);
        response.addCookie(createCookie("refresh",refreshToken));

        log.info(response.getHeader("access"));
        response.setStatus(HttpStatus.OK.value());


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

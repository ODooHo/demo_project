package com.example.demo.global.config.filter;

import com.example.demo.api.user.UserRole;
import com.example.demo.api.user.dto.UserDto;
import com.example.demo.global.auth.oauth.CustomOAuth2User;
import com.example.demo.global.config.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

/*
     헤더 "access"에 추가해야지 파싱 가능함. authorization은 oauth가 사용중이니.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("access");

        if(token == null){
            filterChain.doFilter(request,response);
            return;
        }

        String originToken = token.substring(7);

        log.info(originToken);

        try{
            if(jwtUtil.isExpired(originToken)){
                PrintWriter writer = response.getWriter();
                writer.println("access token expired");

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }catch (ExpiredJwtException e){
            PrintWriter writer = response.getWriter();
            writer.println("access token expired");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String category = jwtUtil.getCategory(originToken);

        if(!"access".equals(category)){
            PrintWriter writer = response.getWriter();
            writer.println("invalid access token");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String username = jwtUtil.getUsername(originToken);
        String role = jwtUtil.getRole(originToken);

        log.info("username : {}, role : {} ",username,role);

        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setRole(UserRole.USER);

        CustomOAuth2User customOAuth2User =  new CustomOAuth2User(userDto);

        Authentication authentication = new UsernamePasswordAuthenticationToken(customOAuth2User,null,customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request,response);


    }
}

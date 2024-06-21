package com.example.demo.api.user.controller;

import com.example.demo.api.response.ResponseDto;
import com.example.demo.api.user.service.AuthService;
import com.example.demo.global.config.util.JwtUtil;
import com.example.demo.global.redis.service.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseDto<?> reissue(HttpServletRequest request, HttpServletResponse response){
        HttpServletResponse result = authService.reissue(request, response);

        return ResponseDto.success(result);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response){
        authService.logout(request,response);
    }


}

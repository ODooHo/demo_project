package com.example.demo.api.user.service;

import com.example.demo.api.exception.auth.AuthErrorCode;
import com.example.demo.api.exception.auth.AuthException;
import com.example.demo.api.user.dto.response.ReissueDto;
import com.example.demo.global.config.util.JwtUtil;
import com.example.demo.global.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;


    @Value("${jwt.access-expired-time}")
    Long accessExpiredTime;

    @Value("${jwt.refresh-expired-time}")
    Long refreshExpiredTime;

    public ReissueDto reissue(String refresh){


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

        return ReissueDto.of(newAccessToken,newRefreshToken);
    }


    public void logout(String refreshToken){


        String key = jwtUtil.getUsername(refreshToken);

        if(redisService.getValues(key) == null) {
            throw new AuthException(AuthErrorCode.NOT_EXISTS_REDIS_REFRESH_TOKEN);
        }

        redisService.deleteValues(key);
    }


}

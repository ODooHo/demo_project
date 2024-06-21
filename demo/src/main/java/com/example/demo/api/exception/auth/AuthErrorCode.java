package com.example.demo.api.exception.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode {
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),
    EXPIRED_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "Access Token expired"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST,"Refresh Token expired"),
    NOT_EXISTS_REDIS_REFRESH_TOKEN(HttpStatus.BAD_REQUEST,"Not exists Redis refresh token"),
    ;

    private final HttpStatus status;
    private final String message;
}


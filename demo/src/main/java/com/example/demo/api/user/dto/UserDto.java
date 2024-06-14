package com.example.demo.api.user.dto;

import com.example.demo.api.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {
    private String email;
    private String password;
    private UserRole role;
    private String username;
}

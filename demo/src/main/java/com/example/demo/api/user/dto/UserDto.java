package com.example.demo.api.user.dto;

import com.example.demo.api.user.UserRole;
import com.example.demo.api.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String email;
    private String password;
    private String username;
    private UserRole role;



    public static UserDto of(String email, String password, String username, UserRole role){
        return new UserDto(
                email,
                password,
                username,
                role
        );
    }

    public static UserDto from(UserEntity entity){
        return UserDto.of(
                entity.getEmail(),
                null,
                entity.getUsername(),
                entity.getRole()
        );
    }
}

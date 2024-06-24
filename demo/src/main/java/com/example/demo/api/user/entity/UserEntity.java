package com.example.demo.api.user.entity;

import com.example.demo.api.user.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *  TODO: 추후 직업군과 직업 정보와 함께 연관관계 매핑 예정 -> 수많은 직업 정보를 어떤식으로 다룰지 생각 필요
 */
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "username")
    private String username;

    @Column(name="role")
    private UserRole role;



    public static UserEntity of(Long id, String email, String password, String username,UserRole role){
        return new UserEntity(id,email,password,username,role);
    }

    public static UserEntity of(String email, String username, UserRole role){
        return UserEntity.of(
                null,
                email,
                null,
                username,
                role
        );
    }

}

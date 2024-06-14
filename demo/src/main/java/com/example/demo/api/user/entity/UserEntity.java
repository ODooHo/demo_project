package com.example.demo.api.user.entity;

import com.example.demo.api.user.UserRole;
import jakarta.persistence.*;
import lombok.Getter;

/**
 *  TODO: 추후 직업군과 직업 정보와 함께 연관관계 매핑 예정 -> 수많은 직업 정보를 어떤식으로 다룰지 생각 필요
 */
@Getter
@Entity
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


}

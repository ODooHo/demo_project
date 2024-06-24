package com.example.demo.api.user.service;

import com.example.demo.api.user.dto.UserDto;
import com.example.demo.api.user.entity.UserEntity;
import com.example.demo.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;


    public UserDto info(String username){
        UserEntity userEntity = userRepository.findByUsername(username);

        return UserDto.from(userEntity);
    }
}

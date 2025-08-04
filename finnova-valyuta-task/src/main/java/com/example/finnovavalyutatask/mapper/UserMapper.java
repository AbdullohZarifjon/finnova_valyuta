package com.example.finnovavalyutatask.mapper;

import com.example.finnovavalyutatask.dto.UserCreateResponseDto;
import com.example.finnovavalyutatask.dto.UserResponseDto;
import com.example.finnovavalyutatask.entity.User;

public class UserMapper {

    public static UserResponseDto toDto(User user) {
        if ( user == null) {
            return null;
        }
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles())
                .build();
    }

    public static UserCreateResponseDto toCreateDto(User user) {
        if ( user == null) {
            return null;
        }
        return UserCreateResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }
}

package com.example.finnovavalyutatask.service;

import com.example.finnovavalyutatask.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface UserService {

    UserCreateResponseDto register(UserRequestDto userRequestDto);

    LoginResponseDto signIn(UserRequestDto loginDTO) throws JsonProcessingException;

    LoginResponseDto refreshToken(RefreshTokenDto dto) throws JsonProcessingException;

    UserResponseDto createUser(UserUpdateRequestDto userUpdateRequestDto);

    UserResponseDto getUserById(Long userId);

    UserResponseDto updateUserForAdmin(Long id, UserUpdateRequestDto userUpdateRequestDto);

    UserResponseDto updateUser(Long id, UserRequestDto userRequestDto);

    void deleteUserById(Long userId);

}

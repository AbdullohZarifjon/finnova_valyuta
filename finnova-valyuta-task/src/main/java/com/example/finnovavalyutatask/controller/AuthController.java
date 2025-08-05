package com.example.finnovavalyutatask.controller;

import com.example.finnovavalyutatask.payload.ApiResponse;
import com.example.finnovavalyutatask.payload.ApiResponseFactory;
import com.example.finnovavalyutatask.payload.dto.response.LoginResponseDto;
import com.example.finnovavalyutatask.payload.dto.request.RefreshTokenDto;
import com.example.finnovavalyutatask.payload.dto.response.UserCreateResponseDto;
import com.example.finnovavalyutatask.payload.dto.request.UserRequestDto;
import com.example.finnovavalyutatask.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<UserCreateResponseDto>> signUp(@RequestBody @Valid UserRequestDto userRequestDto) {
        UserCreateResponseDto createdUser = userService.register(userRequestDto);
        return ApiResponseFactory.success("Foydalanuvchi muvaffaqiyatli yaratildi", createdUser);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<LoginResponseDto>> signIn(@RequestBody @Valid UserRequestDto loginDTO)
            throws JsonProcessingException {
        System.out.println(loginDTO);
        LoginResponseDto tokens = userService.signIn(loginDTO);
        return ApiResponseFactory.success("Tizimga muvaffaqiyatli kirildi", tokens);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponseDto>> refreshToken(@RequestBody @Valid RefreshTokenDto dto)
            throws JsonProcessingException {
        LoginResponseDto newTokens = userService.refreshToken(dto);
        return ApiResponseFactory.success("Token yangilandi", newTokens);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<ApiResponse<Void>> signOut() {
        return ApiResponseFactory.success("Tizimdan muvaffaqiyatli chiqildi");
    }
}

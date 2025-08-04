package com.example.finnovavalyutatask.controller;

import com.example.finnovavalyutatask.dto.LoginResponseDto;
import com.example.finnovavalyutatask.dto.RefreshTokenDto;
import com.example.finnovavalyutatask.dto.UserCreateResponseDto;
import com.example.finnovavalyutatask.dto.UserRequestDto;
import com.example.finnovavalyutatask.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;


    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserCreateResponseDto> signUp(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.register(userRequestDto));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<LoginResponseDto> signIn(@RequestBody UserRequestDto loginDTO) throws JsonProcessingException {
        return ResponseEntity.ok(userService.signIn(loginDTO));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponseDto> refreshToken(@RequestBody RefreshTokenDto dto) throws JsonProcessingException {
        return ResponseEntity.ok(userService.refreshToken(dto));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<?> signOut() {
        return ResponseEntity.ok("You successfully logged out");
    }
}

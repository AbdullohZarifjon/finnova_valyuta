package com.example.finnovavalyutatask.controller;

import com.example.finnovavalyutatask.dto.*;
import com.example.finnovavalyutatask.entity.Role;
import com.example.finnovavalyutatask.entity.enums.UserRole;
import com.example.finnovavalyutatask.service.JwtService;
import com.example.finnovavalyutatask.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @Test
    void signUp_shouldReturnCreatedUser() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("john", "1234");
        UserCreateResponseDto responseDto = new UserCreateResponseDto(1L, "john", "1234");

        Mockito.when(userService.register(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john")))
                .andExpect(jsonPath("$.password", is("1234")));
    }

    @Test
    void signIn_shouldReturnTokens() throws Exception {
        UserRequestDto loginDto = new UserRequestDto("admin", "admin123");
        LoginResponseDto tokenResponse = new LoginResponseDto("access-token", "refresh-token");

        Mockito.when(userService.signIn(loginDto)).thenReturn(tokenResponse);

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", is("access-token")))
                .andExpect(jsonPath("$.refreshToken", is("refresh-token")));
    }

    @Test
    void refreshToken_shouldReturnNewTokens() throws Exception {
        RefreshTokenDto refreshDto = new RefreshTokenDto("old-refresh-token");
        LoginResponseDto newToken = new LoginResponseDto("new-access", "new-refresh");

        Mockito.when(userService.refreshToken(refreshDto)).thenReturn(newToken);

        mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", is("new-access")))
                .andExpect(jsonPath("$.refreshToken", is("new-refresh")));
    }

    @Test
    void signOut_shouldReturnLogoutMessage() throws Exception {
        mockMvc.perform(post("/api/auth/sign-out"))
                .andExpect(status().isOk())
                .andExpect(content().string("You successfully logged out"));
    }
}

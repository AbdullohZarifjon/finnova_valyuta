package com.example.finnovavalyutatask.controller;

import com.example.finnovavalyutatask.payload.dto.request.RefreshTokenDto;
import com.example.finnovavalyutatask.payload.dto.request.UserRequestDto;
import com.example.finnovavalyutatask.payload.dto.response.LoginResponseDto;
import com.example.finnovavalyutatask.payload.dto.response.UserCreateResponseDto;
import com.example.finnovavalyutatask.service.JwtService;
import com.example.finnovavalyutatask.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("✅ signUp: Foydalanuvchi muvaffaqiyatli ro'yxatdan o'tdi")
    void signUp_shouldReturnCreatedUser() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("john", "1234");
        UserCreateResponseDto responseDto = new UserCreateResponseDto(1L, "john", "1234");

        Mockito.when(userService.register(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Foydalanuvchi muvaffaqiyatli yaratildi")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.username", is("john")))
                .andExpect(jsonPath("$.data.password", is("1234")));
    }

    @Test
    @DisplayName("✅ signIn: Foydalanuvchi tizimga muvaffaqiyatli kirdi")
    void signIn_shouldReturnTokens() throws Exception {
        UserRequestDto loginDto = new UserRequestDto("admin", "admin123");
        LoginResponseDto tokenResponse = new LoginResponseDto("access-token", "refresh-token");

        Mockito.when(userService.signIn(loginDto)).thenReturn(tokenResponse);

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Tizimga muvaffaqiyatli kirildi")))
                .andExpect(jsonPath("$.data.accessToken", is("access-token")))
                .andExpect(jsonPath("$.data.refreshToken", is("refresh-token")));
    }

    @Test
    @DisplayName("✅ refreshToken: Yangi tokenlar qaytarildi")
    void refreshToken_shouldReturnNewTokens() throws Exception {
        RefreshTokenDto refreshDto = new RefreshTokenDto("old-refresh-token");
        LoginResponseDto newToken = new LoginResponseDto("new-access", "new-refresh");

        Mockito.when(userService.refreshToken(refreshDto)).thenReturn(newToken);

        mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Token yangilandi")))
                .andExpect(jsonPath("$.data.accessToken", is("new-access")))
                .andExpect(jsonPath("$.data.refreshToken", is("new-refresh")));
    }

    @Test
    @DisplayName("✅ signOut: Tizimdan chiqish xabari muvaffaqiyatli qaytarildi")
    void signOut_shouldReturnLogoutMessage() throws Exception {
        mockMvc.perform(post("/api/auth/sign-out"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Tizimdan muvaffaqiyatli chiqildi")))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}

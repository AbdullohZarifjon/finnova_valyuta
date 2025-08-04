package com.example.finnovavalyutatask.controller;

import com.example.finnovavalyutatask.dto.UserRequestDto;
import com.example.finnovavalyutatask.dto.UserResponseDto;
import com.example.finnovavalyutatask.dto.UserUpdateRequestDto;
import com.example.finnovavalyutatask.entity.Role;
import com.example.finnovavalyutatask.entity.enums.UserRole;
import com.example.finnovavalyutatask.exps.RecordNotFoundException;
import com.example.finnovavalyutatask.filter.JwtAuthenticationFilter;
import com.example.finnovavalyutatask.service.JwtService;
import com.example.finnovavalyutatask.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserResponseDto createSampleUser() {
        return new UserResponseDto(1L, "user", "1234", List.of(new Role(1L, UserRole.ROLE_USER)));
    }

    @Nested
    class AdminTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        void createUser_WithValidRequest_ShouldReturnUser() throws Exception {
            UserUpdateRequestDto requestDto = new UserUpdateRequestDto("newuser", "1233", List.of(2L));
            UserResponseDto responseDto = createSampleUser();
            when(userService.createUser(any(UserUpdateRequestDto.class))).thenReturn(responseDto);
            Claims claims = mock(Claims.class);
            when(claims.get("roles", List.class)).thenReturn(List.of(new Role(1L, UserRole.ROLE_ADMIN)));
            when(jwtService.accessTokenClaims(anyString())).thenReturn(claims);
            doNothing().when(jwtService).validateAccessToken(anyString());

            mockMvc.perform(post("/api/users")
                            .header("Authorization", "Bearer dummy-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createUser_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
            UserUpdateRequestDto invalidRequest = new UserUpdateRequestDto("", "", List.of());

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateUser_WithValidRequest_ShouldReturnUpdatedUser() throws Exception {
            UserUpdateRequestDto requestDto = new UserUpdateRequestDto("updatedUser", "newpass", List.of(2L));
            UserResponseDto responseDto = createSampleUser();
            when(userService.updateUserForAdmin(eq(1L), any(UserUpdateRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(put("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        void deleteUser_WithValidId_ShouldReturnNoContent() throws Exception {
            doNothing().when(userService).deleteUserById(1L);

            mockMvc.perform(delete("/api/users/1"));
        }
    }

    @Nested
    class UserTests {

        @Test
        @WithMockUser
        void updateUser_WithValidRequest_ShouldReturnUpdatedUser() throws Exception {
            UserRequestDto requestDto = new UserRequestDto("user", "newpass");
            UserResponseDto responseDto = createSampleUser();
            when(userService.updateUser(eq(1L), any(UserRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(put("/api/users/update/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)));
        }

        @Test
        void updateUser_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
            UserRequestDto requestDto = new UserRequestDto("user", "newpass");

            mockMvc.perform(put("/api/users/update/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)));
        }
    }

    @Nested
    class GeneralTests {

        @Test
        @WithMockUser
        void getUserById_WithValidId_ShouldReturnUser() throws Exception {
            UserResponseDto responseDto = createSampleUser();
            when(userService.getUserById(1L)).thenReturn(responseDto);

            mockMvc.perform(get("/api/users/1"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void getUserById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
            when(userService.getUserById(999L)).thenThrow(new RecordNotFoundException("User not found"));

            mockMvc.perform(get("/api/users/999"));
        }

        @Test
        void getUserById_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/api/users/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void createUser_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
            UserUpdateRequestDto requestDto = new UserUpdateRequestDto("user", "1234", List.of(1L));

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void updateUserForAdmin_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
            UserUpdateRequestDto requestDto = new UserUpdateRequestDto("user", "1234", List.of(1L));

            mockMvc.perform(put("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteUser_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
            mockMvc.perform(delete("/api/users/1"))
                    .andExpect(status().isForbidden());
        }
    }
}
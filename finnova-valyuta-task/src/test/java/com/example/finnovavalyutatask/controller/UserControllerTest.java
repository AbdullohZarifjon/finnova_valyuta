package com.example.finnovavalyutatask.controller;

import com.example.finnovavalyutatask.payload.dto.request.UserRequestDto;
import com.example.finnovavalyutatask.payload.dto.request.UserUpdateRequestDto;
import com.example.finnovavalyutatask.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String accessTokenAdmin;
    private String accessTokenUser;

    @BeforeEach
    void setUp() throws Exception {
        accessTokenAdmin = obtainToken("admin", "admin");
        accessTokenUser = obtainToken("string", "string");
    }

    private String obtainToken(String username, String password) throws Exception {
        var loginPayload = """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(username, password);

        var response = mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return "Bearer " + objectMapper.readTree(response).get("data").get("accessToken").asText();

    }

    @Test
    void adminCanCreateUser() throws Exception {
        var request = new UserUpdateRequestDto("newuser", "password", List.of(1L));

        mockMvc.perform(post("/api/users")
                        .header("Authorization", accessTokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Foydalanuvchi yaratildi"))
                .andExpect(jsonPath("$.data.username").value("newuser"));
    }

    @Test
    void nonAdminCannotCreateUser() throws Exception {
        var request = new UserUpdateRequestDto("forbiddenUser", "password", List.of(1L));

        mockMvc.perform(post("/api/users")
                        .header("Authorization", accessTokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedUserCanGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", accessTokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Foydalanuvchi topildi"));
    }

    @Test
    void unauthenticatedUserCannotAccessProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminCanUpdateUserFully() throws Exception {
        var updateDto = new UserUpdateRequestDto("updatedAdminUser", "newpass", List.of(1L));

        mockMvc.perform(put("/api/users/3")
                        .header("Authorization", accessTokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Foydalanuvchi yangilandi"));
    }

    @Test
    void userCanUpdateOwnAccount() throws Exception {
        var updateDto = new UserRequestDto("updatedUser", "newpass");

        mockMvc.perform(put("/api/users/update/4")
                        .header("Authorization", accessTokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profil yangilandi"));
    }

    @Test
    void adminCanDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/5")
                        .header("Authorization", accessTokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Foydalanuvchi o'chirildi"));
    }

    @Test
    void userCannotDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/4")
                        .header("Authorization", accessTokenUser))
                .andExpect(status().isForbidden());
    }
}

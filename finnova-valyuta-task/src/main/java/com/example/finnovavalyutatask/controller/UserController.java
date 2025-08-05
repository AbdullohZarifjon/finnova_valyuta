package com.example.finnovavalyutatask.controller;

import com.example.finnovavalyutatask.payload.ApiResponse;
import com.example.finnovavalyutatask.payload.ApiResponseFactory;
import com.example.finnovavalyutatask.payload.dto.request.UserRequestDto;
import com.example.finnovavalyutatask.payload.dto.response.UserResponseDto;
import com.example.finnovavalyutatask.payload.dto.request.UserUpdateRequestDto;
import com.example.finnovavalyutatask.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin: Create a new user")
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(@RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto) {
        return ApiResponseFactory.success("Foydalanuvchi yaratildi", userService.createUser(userUpdateRequestDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable @Min(1) Long id) {
        return ApiResponseFactory.success("Foydalanuvchi topildi", userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin: Fully update a user")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateForAdmin(@PathVariable @Min(1) Long id,
                                                          @RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto) {
        return ApiResponseFactory.success("Foydalanuvchi yangilandi", userService.updateUserForAdmin(id, userUpdateRequestDto));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update own account")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@PathVariable @Min(1) Long id,
                                                      @RequestBody @Valid UserRequestDto userRequestDto) {
        return ApiResponseFactory.success("Profil yangilandi", userService.updateUser(id, userRequestDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin: Delete a user by ID")
    public ResponseEntity<ApiResponse<Void>> deleteUserById(@PathVariable @Min(1) Long id) {
        userService.deleteUserById(id);
        return ApiResponseFactory.success("Foydalanuvchi o'chirildi");
    }
}

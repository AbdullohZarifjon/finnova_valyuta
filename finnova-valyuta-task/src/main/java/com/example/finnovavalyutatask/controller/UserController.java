package com.example.finnovavalyutatask.controller;

import com.example.finnovavalyutatask.dto.UserRequestDto;
import com.example.finnovavalyutatask.dto.UserResponseDto;
import com.example.finnovavalyutatask.dto.UserUpdateRequestDto;
import com.example.finnovavalyutatask.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserUpdateRequestDto userUpdateRequestDto) {
        return ResponseEntity.ok(userService.createUser(userUpdateRequestDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin: Fully update a user")
    public ResponseEntity<UserResponseDto> updateForAdmin(@PathVariable Long id,
                                                          @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
        return ResponseEntity.ok(userService.updateUserForAdmin(id, userUpdateRequestDto));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update own account")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id,
                                                      @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.updateUser(id, userRequestDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin: Delete a user by ID")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}

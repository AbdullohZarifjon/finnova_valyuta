package com.example.finnovavalyutatask.dto;

import com.example.finnovavalyutatask.entity.Role;
import lombok.Builder;

import java.util.List;

@Builder
public record UserResponseDto(Long id, String username, String password, List<Role> roles) {
}

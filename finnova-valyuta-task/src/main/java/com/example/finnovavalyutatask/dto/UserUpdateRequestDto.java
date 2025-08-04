package com.example.finnovavalyutatask.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UserUpdateRequestDto(String username, String password, List<Long> roleIds) {
}

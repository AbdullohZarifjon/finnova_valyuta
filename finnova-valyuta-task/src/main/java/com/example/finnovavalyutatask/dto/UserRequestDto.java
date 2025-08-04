package com.example.finnovavalyutatask.dto;

import lombok.Builder;

@Builder
public record UserRequestDto(String username, String password) {
}

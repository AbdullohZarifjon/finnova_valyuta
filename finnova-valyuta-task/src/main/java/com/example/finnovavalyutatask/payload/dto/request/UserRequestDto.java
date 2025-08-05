package com.example.finnovavalyutatask.payload.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserRequestDto(
        @NotBlank(message = "Username bo‘sh bo‘lishi mumkin emas")
        String username,

        @NotBlank(message = "Parol bo‘sh bo‘lishi mumkin emas")
        String password
) {
}

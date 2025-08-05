package com.example.finnovavalyutatask.payload.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record UserUpdateRequestDto(

        @NotBlank(message = "Username bo‘sh bo‘lishi mumkin emas")
        String username,

        @NotBlank(message = "Parol bo‘sh bo‘lishi mumkin emas")
        String password,

        @NotNull(message = "Role ID lar bo‘sh bo‘lishi mumkin emas")
        List<@NotNull(message = "Role ID bo‘sh bo‘lishi mumkin emas")
        @Positive(message = "Role ID musbat son bo‘lishi kerak") Long> roleIds

) {
}

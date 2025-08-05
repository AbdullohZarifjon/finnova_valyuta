package com.example.finnovavalyutatask.payload.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RefreshTokenDto {

    @NotBlank(message = "Refresh token bo‘sh bo‘lishi mumkin emas")
    private String refreshToken;
}

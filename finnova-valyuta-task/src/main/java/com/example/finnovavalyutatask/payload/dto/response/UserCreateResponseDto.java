package com.example.finnovavalyutatask.payload.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateResponseDto {
    private Long id;
    private String username;
    private String password;
}

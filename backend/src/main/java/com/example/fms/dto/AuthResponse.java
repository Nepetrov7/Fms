package com.example.fms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ответ после регистрации или входа пользователя.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String login;
}

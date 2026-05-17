package com.example.fms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Тело запроса входа в систему.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Логин обязателен")
    private String login;

    @NotBlank(message = "Пароль обязателен")
    private String password;
}

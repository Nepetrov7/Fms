package com.example.fms.controller;

import com.example.fms.dto.AuthResponse;
import com.example.fms.dto.LoginRequest;
import com.example.fms.dto.RegisterRequest;
import com.example.fms.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API регистрации и входа пользователей.
 *
 * <p>Оба метода возвращают JWT, который фронтенд затем передает в заголовке
 * {@code Authorization: Bearer ...} для защищенных запросов.</p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Регистрирует нового пользователя и создает для него пустой профиль мигранта.
     *
     * @param request логин и пароль нового пользователя
     * @return JWT и краткие данные пользователя
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    /**
     * Выполняет вход пользователя по логину и паролю.
     *
     * @param request учетные данные пользователя
     * @return JWT и краткие данные пользователя
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }
}

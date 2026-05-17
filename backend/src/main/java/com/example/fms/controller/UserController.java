package com.example.fms.controller;

import com.example.fms.dto.UserProfileResponse;
import com.example.fms.dto.UserProfileUpdateRequest;
import com.example.fms.service.UserServiceApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API профиля текущего авторизованного пользователя.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    private final UserServiceApi userService;

    /**
     * Возвращает профиль текущего пользователя.
     *
     * @return данные мигранта, сведения о патенте и признак заполненности профиля
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }

    /**
     * Обновляет профиль мигранта текущего пользователя.
     *
     * @param request новые значения полей профиля
     * @return обновленный профиль
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody UserProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }
}

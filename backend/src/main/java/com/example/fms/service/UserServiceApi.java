package com.example.fms.service;

import com.example.fms.dto.UserProfileResponse;
import com.example.fms.dto.UserProfileUpdateRequest;
import com.example.fms.entity.User;

/**
 * Контракт работы с текущим авторизованным пользователем и его профилем мигранта.
 */
public interface UserServiceApi {

    /**
     * Загружает текущего пользователя из контекста безопасности и базы данных.
     *
     * @return сущность пользователя
     */
    User getCurrentUser();

    /**
     * Возвращает профиль текущего пользователя.
     *
     * @return DTO профиля мигранта
     */
    UserProfileResponse getProfile();

    /**
     * Обновляет профиль мигранта по данным запроса.
     *
     * @param request данные для сохранения
     * @return обновленный DTO профиля
     */
    UserProfileResponse updateProfile(UserProfileUpdateRequest request);
}

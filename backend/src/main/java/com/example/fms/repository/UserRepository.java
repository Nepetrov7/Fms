package com.example.fms.repository;

import com.example.fms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий учетных записей пользователей.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Ищет пользователя по логину.
     *
     * @param login уникальный логин пользователя
     * @return пользователь, если найден
     */
    Optional<User> findByLogin(String login);

    /**
     * Проверяет, занят ли логин.
     *
     * @param login проверяемый логин
     * @return {@code true}, если пользователь с таким логином уже существует
     */
    boolean existsByLogin(String login);
}

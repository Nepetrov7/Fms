package com.example.fms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Учетная запись пользователя системы.
 *
 * <p>После успешной JWT-аутентификации экземпляр этой сущности используется как
 * principal в Spring Security.</p>
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Уникальный логин пользователя. */
    @Column(nullable = false, unique = true)
    private String login;

    /** Хеш пароля BCrypt; не сериализуется в JSON. */
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    /** Признак доступа к административной панели. */
    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false;
}

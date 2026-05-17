package com.example.fms.security;

import com.example.fms.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * Создает и проверяет JWT для аутентификации пользователей FMS.
 *
 * <p>Токен подписывается алгоритмом HS256. В {@code subject} хранится логин,
 * а в claim {@code uid} - идентификатор пользователя.</p>
 */
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;

    /**
     * Создает провайдер JWT на основе секрета из конфигурации.
     *
     * @param secret секрет подписи HS256 длиной не менее 32 байт
     * @param expirationMs срок жизни токена в миллисекундах
     */
    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("app.jwt.secret must be at least 32 bytes for HS256");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.expirationMs = expirationMs;
    }

    /**
     * Выпускает подписанный JWT для пользователя.
     *
     * @param user учетная запись пользователя
     * @return компактная строка JWT
     */
    public String generateToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getLogin())
                .claim("uid", user.getId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key)
                .compact();
    }

    /**
     * Извлекает логин пользователя из токена.
     *
     * @param token строка JWT
     * @return логин пользователя из {@code subject}
     */
    public String getLoginFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Разбирает токен и проверяет его подпись.
     *
     * @param token строка JWT
     * @return claims payload токена
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Проверяет, что токен корректен, подписан правильным ключом и не истек.
     *
     * @param token строка JWT
     * @return {@code true}, если токен валиден
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

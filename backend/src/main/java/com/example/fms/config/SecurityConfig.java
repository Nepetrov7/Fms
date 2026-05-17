package com.example.fms.config;

import com.example.fms.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Настраивает безопасность HTTP-запросов приложения.
 *
 * <p>Приложение работает без серверных сессий: пользователь получает JWT при
 * входе, а затем передает его в каждом защищенном запросе. Фильтр
 * {@link JwtAuthenticationFilter} извлекает токен и устанавливает пользователя
 * в контекст Spring Security.</p>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Создает цепочку фильтров Spring Security.
     *
     * @param http объект настройки HTTP-безопасности
     * @return настроенная цепочка фильтров
     * @throws Exception если конфигурация безопасности не может быть собрана
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/admin/**").permitAll()
                        .requestMatchers("/api/reference/**").permitAll()
                        .requestMatchers("/api/user/**", "/api/roadmap", "/api/roadmap/**").authenticated()
                        .anyRequest().permitAll())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .exceptionHandling(e -> e.authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"message\":\"Требуется авторизация\"}");
                }))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Создает кодировщик паролей для регистрации и входа.
     *
     * @return BCrypt-кодировщик паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

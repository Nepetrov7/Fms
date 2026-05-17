package com.example.fms.service;

import com.example.fms.dto.AuthResponse;
import com.example.fms.dto.LoginRequest;
import com.example.fms.dto.RegisterRequest;
import com.example.fms.entity.Migrant;
import com.example.fms.entity.User;
import com.example.fms.repository.MigrantRepository;
import com.example.fms.repository.UserRepository;
import com.example.fms.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Выполняет регистрацию и вход пользователей с выдачей JWT.
 *
 * <p>При регистрации сервис создает не только учетную запись, но и пустой
 * профиль мигранта, чтобы пользователь мог сразу перейти к заполнению анкеты.</p>
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final MigrantRepository migrantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Регистрирует пользователя с уникальным логином и хешированным паролем.
     *
     * @param request логин и пароль нового пользователя
     * @return JWT и краткие данные созданной учетной записи
     * @throws IllegalArgumentException если логин уже занят
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String login = request.getLogin().trim();
        if (userRepository.existsByLogin(login)) {
            throw new IllegalArgumentException("Этот логин уже занят");
        }
        User user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsAdmin(false);
        user = userRepository.save(user);

        Migrant migrant = new Migrant();
        migrant.setUser(user);
        migrantRepository.save(migrant);

        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getLogin());
    }

    /**
     * Проверяет логин и пароль пользователя и выдает JWT.
     *
     * @param request учетные данные для входа
     * @return JWT и краткие данные пользователя
     * @throws BadCredentialsException если логин или пароль неверные
     */
    public AuthResponse login(LoginRequest request) {
        String login = request.getLogin().trim();
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new BadCredentialsException("Неверный логин или пароль"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Неверный логин или пароль");
        }
        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getLogin());
    }
}

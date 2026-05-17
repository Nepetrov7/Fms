package com.example.fms.security;

import com.example.fms.entity.User;
import com.example.fms.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Фильтр, который извлекает JWT из заголовка {@code Authorization}.
 *
 * <p>Если токен валиден, фильтр загружает пользователя из базы и устанавливает
 * {@link UsernamePasswordAuthenticationToken} в {@link SecurityContextHolder}.
 * Для защищенных API некорректный токен приводит к HTTP 401.</p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    /**
     * Обрабатывает один HTTP-запрос и при наличии валидного JWT заполняет контекст безопасности.
     *
     * @param request входящий HTTP-запрос
     * @param response HTTP-ответ
     * @param filterChain следующая часть цепочки фильтров
     * @throws ServletException при ошибке servlet-обработки
     * @throws IOException при ошибке чтения или записи ответа
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.substring(7).trim();
        if (!jwtTokenProvider.validateToken(token)) {
            if (isProtectedApiPath(request.getRequestURI())) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String login = jwtTokenProvider.getLoginFromToken(token);
            userRepository.findByLogin(login).ifPresent(user -> authenticate(user, request));
        } catch (Exception e) {
            if (isProtectedApiPath(request.getRequestURI())) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Помещает пользователя в контекст безопасности Spring.
     *
     * @param user пользователь, найденный по логину из JWT
     * @param request текущий HTTP-запрос для заполнения деталей аутентификации
     */
    private void authenticate(User user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * Проверяет, относится ли путь к API, где отсутствие авторизации должно вернуть 401.
     *
     * @param uri путь запроса
     * @return {@code true}, если путь защищен JWT
     */
    private boolean isProtectedApiPath(String uri) {
        return uri.startsWith("/api/user/") || uri.startsWith("/api/roadmap");
    }
}

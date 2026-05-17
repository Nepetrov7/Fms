package com.example.fms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Настраивает раздачу собранного React SPA через Spring Boot.
 *
 * <p>Если пользователь открывает прямую ссылку вроде {@code /roadmap} или
 * {@code /admin/tasks}, Spring должен отдать {@code index.html}, чтобы роутинг
 * обработал React Router. API и H2-console из этого fallback исключены.</p>
 */
@Configuration
public class SpaWebConfig implements WebMvcConfigurer {

    /**
     * Добавляет обработчик статических ресурсов и fallback на {@code index.html}.
     *
     * @param registry реестр обработчиков ресурсов Spring MVC
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    /**
                     * Возвращает запрошенный статический ресурс или fallback для SPA-маршрута.
                     *
                     * @param resourcePath путь ресурса относительно {@code classpath:/static/}
                     * @param location корневая директория статических ресурсов
                     * @return найденный ресурс, {@code index.html} для SPA или {@code null} для API
                     * @throws IOException если ресурс невозможно прочитать
                     */
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        if (resourcePath.startsWith("api/") || resourcePath.startsWith("h2-console")) {
                            return null;
                        }
                        Resource requested = location.createRelative(resourcePath);
                        if (requested.exists() && requested.isReadable()) {
                            return requested;
                        }
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}

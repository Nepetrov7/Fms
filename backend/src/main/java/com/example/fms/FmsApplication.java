package com.example.fms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главная точка входа Spring Boot-приложения FMS.
 *
 * <p>Запуск этого класса поднимает встроенный веб-сервер, REST API,
 * Spring Security, JPA-репозитории и начальную инициализацию данных,
 * которые используются backend-частью и React-фронтендом.</p>
 */
@SpringBootApplication
public class FmsApplication {

    /**
     * Запускает приложение с аргументами командной строки, переданными JVM.
     *
     * @param args аргументы командной строки для Spring Boot и JVM
     */
    public static void main(String[] args) {
        SpringApplication.run(FmsApplication.class, args);
    }
}

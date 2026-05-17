package com.example.fms.controller;

import com.example.fms.service.reference.CountryReferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Публичный REST API справочников для форм.
 */
@RestController
@RequestMapping("/api/reference")
@CrossOrigin(origins = "*")
public class ReferenceController {

    private final CountryReferenceService countryReferenceService;

    /**
     * Создает контроллер справочников.
     *
     * @param countryReferenceService сервис поиска стран
     */
    public ReferenceController(CountryReferenceService countryReferenceService) {
        this.countryReferenceService = countryReferenceService;
    }

    /**
     * Ищет страны для автодополнения в профиле.
     *
     * @param q начало или часть названия страны
     * @param limit максимальное количество результатов
     * @return список подходящих названий стран
     */
    @GetMapping("/countries")
    public ResponseEntity<List<String>> searchCountries(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(countryReferenceService.search(q, limit));
    }
}

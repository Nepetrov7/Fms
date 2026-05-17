package com.example.fms.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST API метаданных для конструктора правил отображения в админ-панели.
 */
@RestController
@RequestMapping("/api/admin/metadata")
@CrossOrigin(origins = "*")
public class AdminMetadataController {

    /**
     * Возвращает параметры профиля, доступные в условиях отображения задач.
     *
     * @return список пар {@code value}/{@code label} для выпадающего списка
     */
    @GetMapping("/parameters")
    public ResponseEntity<List<Map<String, String>>> getParameters() {
        return ResponseEntity.ok(List.of(
                Map.of("value", "ALL", "label", "Для всех"),
                Map.of("value", "DAYS_SINCE_ENTRY", "label", "Дней с момента въезда"),
                Map.of("value", "HAS_PATENT", "label", "Наличие патента"),
                Map.of("value", "HAS_LANGUAGE_CERTIFICATE", "label", "Сертификат русского языка"),
                Map.of("value", "COUNTRY_OF_ARRIVAL", "label", "Страна, откуда прибыл"),
                Map.of("value", "CITIZENSHIP", "label", "Гражданство")));
    }

    /**
     * Возвращает операторы сравнения, доступные в условиях отображения задач.
     *
     * @return список пар {@code value}/{@code label} для выпадающего списка
     */
    @GetMapping("/operators")
    public ResponseEntity<List<Map<String, String>>> getOperators() {
        return ResponseEntity.ok(List.of(
                Map.of("value", "EQ", "label", "Равно"),
                Map.of("value", "NE", "label", "Не равно"),
                Map.of("value", "GT", "label", "Больше"),
                Map.of("value", "LT", "label", "Меньше"),
                Map.of("value", "GTE", "label", "Больше или равно"),
                Map.of("value", "LTE", "label", "Меньше или равно")));
    }
}

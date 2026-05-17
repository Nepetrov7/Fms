package com.example.fms.controller;

import com.example.fms.dto.PatentTypeDto;
import com.example.fms.repository.PatentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API справочника активных типов патента.
 */
@RestController
@RequestMapping("/api/patent-types")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatentTypeController {

    private final PatentTypeRepository patentTypeRepository;

    /**
     * Возвращает активные типы патента для формы профиля.
     *
     * @return список типов патента, отсортированный по названию
     */
    @GetMapping
    public ResponseEntity<List<PatentTypeDto>> listActive() {
        List<PatentTypeDto> types = patentTypeRepository.findAllByActiveTrueOrderByNameAsc().stream()
                .map(t -> new PatentTypeDto(t.getId(), t.getCode(), t.getName()))
                .toList();
        return ResponseEntity.ok(types);
    }
}

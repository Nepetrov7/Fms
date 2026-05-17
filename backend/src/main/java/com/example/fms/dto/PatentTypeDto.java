package com.example.fms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Элемент публичного справочника типов патента.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatentTypeDto {
    private Long id;
    private String code;
    private String name;
}

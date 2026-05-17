package com.example.fms.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Одно условие правила отображения в ответе админского API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayRuleConditionDto {
    private Long id;
    private String parameterKey;
    private String operator;
    private String value;
}

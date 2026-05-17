package com.example.fms.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Условие правила отображения при создании или обновлении группы правил.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayRuleConditionRequest {
    private String parameterKey;
    private String operator;
    private String value;
}

package com.example.fms.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Запрос на сохранение группы правил отображения.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayRuleGroupRequest {
    private List<DisplayRuleConditionRequest> conditions;
}

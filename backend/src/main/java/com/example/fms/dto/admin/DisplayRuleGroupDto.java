package com.example.fms.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Группа условий отображения задачи дорожной карты.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayRuleGroupDto {
    private Integer ruleGroupId;
    private Long taskId;
    private List<DisplayRuleConditionDto> conditions;
}

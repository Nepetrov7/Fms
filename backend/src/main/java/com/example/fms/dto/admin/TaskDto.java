package com.example.fms.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Задача дорожной карты в админском API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    private Long id;
    private String title;
    private String groupName;
    private String description;
    private Integer daysToComplete;
    private Boolean active;
}

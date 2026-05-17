package com.example.fms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Задача дорожной карты в ответе пользовательского API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoadmapTaskDto {
    private Long id;
    private String title;
    private String groupName;
    private String description;
    private Integer daysToComplete;
}

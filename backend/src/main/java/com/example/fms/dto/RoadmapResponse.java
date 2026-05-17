package com.example.fms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * Агрегированный ответ API дорожной карты.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoadmapResponse {
    private List<RoadmapTaskDto> tasks;
    private Set<Long> completedTaskIds;
    private Boolean isProfileComplete;
    private String message;
}

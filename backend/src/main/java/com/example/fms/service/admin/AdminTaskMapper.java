package com.example.fms.service.admin;

import com.example.fms.dto.admin.TaskDto;
import com.example.fms.entity.RoadmapTask;
import org.springframework.stereotype.Component;

/**
 * Преобразует сущности задач дорожной карты в DTO для админского API.
 */
@Component
public class AdminTaskMapper {

    /**
     * Собирает DTO из сущности задачи.
     *
     * @param task задача из базы данных
     * @return DTO задачи для ответа API
     */
    public TaskDto toDto(RoadmapTask task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getGroupName(),
                task.getDescription(),
                task.getDaysToComplete(),
                task.getActive());
    }
}

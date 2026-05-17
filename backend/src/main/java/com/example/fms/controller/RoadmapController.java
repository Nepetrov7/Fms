package com.example.fms.controller;

import com.example.fms.dto.RoadmapResponse;
import com.example.fms.service.RoadmapServiceApi;
import com.example.fms.service.TaskCompletionServiceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API дорожной карты для авторизованного мигранта.
 */
@RestController
@RequestMapping("/api/roadmap")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RoadmapController implements RoadmapControllerApi {
    private final RoadmapServiceApi roadmapService;
    private final TaskCompletionServiceApi taskCompletionService;

    /**
     * Возвращает дорожную карту текущего пользователя.
     *
     * @return ответ с задачами, выполненными пунктами и сообщением для UI
     */
    @GetMapping
    public ResponseEntity<RoadmapResponse> getRoadmap() {
        try {
            RoadmapResponse response = roadmapService.getRoadmap();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Отмечает задачу выполненной.
     *
     * @param taskId идентификатор задачи дорожной карты
     * @return пустой ответ при успехе
     */
    @PostMapping("/tasks/{taskId}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable Long taskId) {
        taskCompletionService.completeTask(taskId);
        return ResponseEntity.ok().build();
    }

    /**
     * Снимает отметку выполнения задачи.
     *
     * @param taskId идентификатор задачи дорожной карты
     * @return пустой ответ при успехе
     */
    @PostMapping("/tasks/{taskId}/uncomplete")
    public ResponseEntity<Void> uncompleteTask(@PathVariable Long taskId) {
        taskCompletionService.uncompleteTask(taskId);
        return ResponseEntity.ok().build();
    }
}

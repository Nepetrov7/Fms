package com.example.fms.service.impl;

import com.example.fms.dto.RoadmapResponse;
import com.example.fms.dto.RoadmapTaskDto;
import com.example.fms.engine.ProfileCompletenessResolver;
import com.example.fms.engine.TaskCompletionResolver;
import com.example.fms.engine.TaskVisibilityResolver;
import com.example.fms.entity.RoadmapTask;
import com.example.fms.entity.User;
import com.example.fms.repository.RoadmapTaskRepository;
import com.example.fms.service.RoadmapMessageService;
import com.example.fms.service.RoadmapServiceApi;
import com.example.fms.service.UserServiceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Формирует дорожную карту для текущего авторизованного пользователя.
 *
 * <p>Сервис проверяет заполненность профиля, получает видимые задачи по правилам
 * отображения, добавляет информацию о выполненных пунктах и выбирает текстовое
 * сообщение для интерфейса.</p>
 */
@Service
@RequiredArgsConstructor
public class RoadmapService implements RoadmapServiceApi {

    private final UserServiceApi userService;
    private final TaskVisibilityResolver visibilityResolver;
    private final TaskCompletionResolver taskCompletionResolver;
    private final ProfileCompletenessResolver profileResolver;
    private final RoadmapMessageService roadmapMessageService;
    private final RoadmapTaskRepository taskRepository;

    /**
     * Возвращает дорожную карту текущего пользователя.
     *
     * @return задачи, идентификаторы выполненных пунктов, признак заполненности профиля и сообщение для UI
     */
    @Override
    public RoadmapResponse getRoadmap() {
        User user = userService.getCurrentUser();

        if (!profileResolver.isProfileComplete(user)) {
            return new RoadmapResponse(
                    List.of(),
                    Set.of(),
                    false,
                    roadmapMessageService.profileIncompleteMessage());
        }

        List<RoadmapTask> visible = visibilityResolver.getVisibleTasks(user);
        boolean hasConfiguredTasks = taskRepository.count() > 0;

        Set<Long> completedIds = taskCompletionResolver.resolveCompletion(user).entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        String message;
        if (!hasConfiguredTasks) {
            message = roadmapMessageService.emptyRoadmapMessage();
        } else if (visible.isEmpty()) {
            message = roadmapMessageService.completeRoadmapMessage();
        } else if (visible.stream().allMatch(t -> completedIds.contains(t.getId()))) {
            message = roadmapMessageService.completeRoadmapMessage();
        } else {
            long done = visible.stream().filter(t -> completedIds.contains(t.getId())).count();
            message = roadmapMessageService.activeRoadmapMessage()
                    + " (" + done + " из " + visible.size() + " выполнено)";
        }

        List<RoadmapTaskDto> tasks = visible.stream().map(this::toDto).toList();
        return new RoadmapResponse(tasks, completedIds, true, message);
    }

    /**
     * Преобразует JPA-сущность задачи в DTO для API дорожной карты.
     *
     * @param task задача из базы данных
     * @return компактное представление задачи для клиента
     */
    private RoadmapTaskDto toDto(RoadmapTask task) {
        return new RoadmapTaskDto(
                task.getId(),
                task.getTitle(),
                task.getGroupName(),
                task.getDescription(),
                task.getDaysToComplete());
    }
}

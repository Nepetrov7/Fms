package com.example.fms.engine;

import com.example.fms.entity.RoadmapTask;
import com.example.fms.entity.User;
import com.example.fms.entity.UserTaskCompletion;
import com.example.fms.repository.RoadmapTaskRepository;
import com.example.fms.repository.UserTaskCompletionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Вычисляет статусы выполнения задач дорожной карты для пользователя.
 *
 * <p>Источник статуса - таблица {@link UserTaskCompletion}. Наличие записи
 * для пары пользователь-задача означает, что пользователь отметил задачу
 * выполненной.</p>
 */
@Component
@RequiredArgsConstructor
public class TaskCompletionResolver {

    private final UserTaskCompletionRepository completionRepository;
    private final RoadmapTaskRepository taskRepository;

    /**
     * Строит карту статусов выполнения для всех задач дорожной карты.
     *
     * @param user авторизованный пользователь
     * @return карта, где ключ - идентификатор задачи, значение - выполнена ли задача
     */
    public Map<Long, Boolean> resolveCompletion(User user) {
        Map<Long, Boolean> result = new HashMap<>();
        List<RoadmapTask> all = taskRepository.findAllByOrderByDaysToCompleteAsc();
        for (RoadmapTask task : all) {
            result.put(task.getId(), isTaskComplete(user, task, result));
        }
        return result;
    }

    /**
     * Проверяет, отмечена ли конкретная задача выполненной для пользователя.
     *
     * @param user авторизованный пользователь
     * @param task задача дорожной карты
     * @param completedCache зарезервированный параметр для будущих зависимостей между задачами
     * @return {@code true}, если для пары пользователь-задача есть запись выполнения
     */
    public boolean isTaskComplete(User user, RoadmapTask task, Map<Long, Boolean> completedCache) {
        return completionRepository.findByUserIdAndTaskId(user.getId(), task.getId()).isPresent();
    }
}

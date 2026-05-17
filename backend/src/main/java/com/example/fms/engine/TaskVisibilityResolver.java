package com.example.fms.engine;

import com.example.fms.entity.DisplayRule;
import com.example.fms.entity.Migrant;
import com.example.fms.entity.RoadmapTask;
import com.example.fms.entity.User;
import com.example.fms.repository.DisplayRuleRepository;
import com.example.fms.repository.MigrantRepository;
import com.example.fms.repository.RoadmapTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Определяет, какие активные задачи дорожной карты нужно показать пользователю.
 *
 * <p>Компонент загружает профиль мигранта, список активных задач и правила
 * отображения для этих задач. Сами условия проверяются в
 * {@link DisplayRuleEvaluator}; этот класс отвечает за сбор данных и фильтрацию
 * списка задач.</p>
 */
@Component
@RequiredArgsConstructor
public class TaskVisibilityResolver {

    private final RoadmapTaskRepository taskRepository;
    private final DisplayRuleRepository displayRuleRepository;
    private final MigrantRepository migrantRepository;
    private final DisplayRuleEvaluator displayRuleEvaluator;

    /**
     * Возвращает активные задачи, правила которых подходят профилю пользователя.
     *
     * @param user авторизованный пользователь
     * @return задачи, отсортированные по сроку выполнения; пустой список, если профиль не найден
     */
    public List<RoadmapTask> getVisibleTasks(User user) {
        Migrant migrant = migrantRepository.findByUser_Id(user.getId())
                .orElse(null);
        if (migrant == null) {
            return List.of();
        }

        List<RoadmapTask> active = taskRepository.findAllByActiveTrueOrderByDaysToCompleteAsc();
        List<Long> taskIds = active.stream().map(RoadmapTask::getId).toList();
        Map<Long, List<DisplayRule>> rulesByTask = displayRuleRepository.findByTaskIdIn(taskIds).stream()
                .collect(Collectors.groupingBy(DisplayRule::getTaskId));

        return active.stream()
                .filter(t -> displayRuleEvaluator.isTaskVisible(migrant, rulesByTask.get(t.getId())))
                .toList();
    }
}

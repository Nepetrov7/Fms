package com.example.fms.service.admin;

import com.example.fms.dto.admin.TaskDto;
import com.example.fms.entity.RoadmapTask;
import com.example.fms.repository.DisplayRuleRepository;
import com.example.fms.repository.RoadmapTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Создает, изменяет и удаляет задачи дорожной карты в админ-панели.
 *
 * <p>Сервис отвечает только за команды изменения состояния. Чтение вынесено
 * в {@link AdminTaskQueryService}, чтобы логика API оставалась проще.</p>
 */
@Service
@RequiredArgsConstructor
public class AdminTaskCommandService {

    private final RoadmapTaskRepository taskRepository;
    private final DisplayRuleRepository displayRuleRepository;
    private final AdminTaskMapper adminTaskMapper;

    /**
     * Создает новую активную задачу дорожной карты.
     *
     * @param title заголовок задачи
     * @param groupName название группы, в которой задача отображается на UI
     * @param description подробное описание действия
     * @param daysToComplete срок выполнения в днях от даты въезда
     * @return DTO созданной задачи
     */
    @Transactional
    public TaskDto createTask(String title, String groupName, String description, Integer daysToComplete) {
        RoadmapTask task = new RoadmapTask();
        task.setTitle(title);
        task.setGroupName(groupName);
        task.setDescription(description);
        task.setDaysToComplete(daysToComplete);
        task.setActive(true);
        return adminTaskMapper.toDto(taskRepository.save(task));
    }

    /**
     * Частично обновляет поля существующей задачи.
     *
     * <p>Параметр {@code null} означает, что соответствующее поле не нужно
     * изменять.</p>
     *
     * @param id идентификатор задачи
     * @param title новый заголовок или {@code null}
     * @param groupName новая группа или {@code null}
     * @param description новое описание или {@code null}
     * @param daysToComplete новый срок или {@code null}
     * @param active новый признак активности или {@code null}
     * @return DTO обновленной задачи
     */
    @Transactional
    public TaskDto updateTask(Long id, String title, String groupName, String description,
            Integer daysToComplete, Boolean active) {
        RoadmapTask task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задача не найдена: " + id));
        if (title != null) {
            task.setTitle(title);
        }
        if (groupName != null) {
            task.setGroupName(groupName);
        }
        if (description != null) {
            task.setDescription(description);
        }
        if (daysToComplete != null) {
            task.setDaysToComplete(daysToComplete);
        }
        if (active != null) {
            task.setActive(active);
        }
        return adminTaskMapper.toDto(taskRepository.save(task));
    }

    /**
     * Удаляет задачу и все связанные с ней правила отображения.
     *
     * @param id идентификатор задачи
     */
    @Transactional
    public void deleteTask(Long id) {
        displayRuleRepository.deleteByTaskId(id);
        taskRepository.deleteById(id);
    }
}

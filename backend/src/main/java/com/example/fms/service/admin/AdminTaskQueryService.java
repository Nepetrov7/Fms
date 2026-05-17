package com.example.fms.service.admin;

import com.example.fms.dto.admin.TaskDto;
import com.example.fms.entity.RoadmapTask;
import com.example.fms.repository.RoadmapTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Читает задачи дорожной карты для админ-панели.
 *
 * <p>В отличие от пользовательской дорожной карты, админка видит все задачи,
 * включая неактивные, чтобы их можно было отредактировать или включить обратно.</p>
 */
@Service
@RequiredArgsConstructor
public class AdminTaskQueryService {

    private final RoadmapTaskRepository taskRepository;
    private final AdminTaskMapper adminTaskMapper;

    /**
     * Возвращает все задачи дорожной карты.
     *
     * @return список DTO задач, отсортированный по сроку выполнения
     */
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAllByOrderByDaysToCompleteAsc().stream()
                .map(adminTaskMapper::toDto)
                .toList();
    }

    /**
     * Возвращает одну задачу по идентификатору.
     *
     * @param id идентификатор задачи
     * @return DTO найденной задачи
     * @throws RuntimeException если задача не найдена
     */
    public TaskDto getTask(Long id) {
        RoadmapTask task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задача не найдена: " + id));
        return adminTaskMapper.toDto(task);
    }

    /**
     * Возвращает уникальные непустые названия групп задач.
     *
     * @return отсортированный список названий групп для автодополнения в админке
     */
    public List<String> getDistinctGroupNames() {
        return taskRepository.findDistinctGroupNamesForAdmin();
    }
}

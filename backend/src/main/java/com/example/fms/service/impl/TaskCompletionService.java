package com.example.fms.service.impl;

import com.example.fms.entity.User;
import com.example.fms.entity.UserTaskCompletion;
import com.example.fms.repository.UserTaskCompletionRepository;
import com.example.fms.service.TaskCompletionServiceApi;
import com.example.fms.service.UserServiceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Управляет отметками выполнения задач дорожной карты для текущего пользователя.
 *
 * <p>Одна запись {@link UserTaskCompletion} означает, что конкретный пользователь
 * отметил конкретную задачу выполненной. Повторная отметка не создает дубль.</p>
 */
@Service
@RequiredArgsConstructor
public class TaskCompletionService implements TaskCompletionServiceApi {

    private final UserServiceApi userService;
    private final UserTaskCompletionRepository completionRepository;

    /**
     * Отмечает задачу выполненной для текущего пользователя.
     *
     * @param taskId идентификатор задачи дорожной карты
     */
    @Transactional
    public void completeTask(Long taskId) {
        User user = userService.getCurrentUser();
        completionRepository.findByUserIdAndTaskId(user.getId(), taskId)
                .orElseGet(() -> {
                    var c = new UserTaskCompletion();
                    c.setUser(user);
                    c.setTaskId(taskId);
                    c.setCompletedAt(Instant.now());
                    return completionRepository.save(c);
                });
    }

    /**
     * Снимает отметку выполнения задачи у текущего пользователя.
     *
     * @param taskId идентификатор задачи дорожной карты
     */
    @Transactional
    public void uncompleteTask(Long taskId) {
        User user = userService.getCurrentUser();
        completionRepository.findByUserIdAndTaskId(user.getId(), taskId).ifPresent(completionRepository::delete);
    }
}

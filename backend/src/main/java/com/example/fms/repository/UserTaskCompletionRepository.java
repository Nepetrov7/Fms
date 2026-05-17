package com.example.fms.repository;

import com.example.fms.entity.UserTaskCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий отметок выполнения задач дорожной карты.
 */
@Repository
public interface UserTaskCompletionRepository extends JpaRepository<UserTaskCompletion, Long> {

    /** Возвращает все выполненные задачи пользователя. */
    List<UserTaskCompletion> findByUserId(Long userId);

    /**
     * Ищет отметку выполнения по паре пользователь-задача.
     *
     * @param userId идентификатор пользователя
     * @param taskId идентификатор задачи дорожной карты
     * @return отметка выполнения, если она существует
     */
    Optional<UserTaskCompletion> findByUserIdAndTaskId(Long userId, Long taskId);
}

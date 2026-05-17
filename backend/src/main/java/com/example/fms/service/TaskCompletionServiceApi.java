package com.example.fms.service;

/**
 * Контракт работы с отметками выполнения задач дорожной карты.
 */
public interface TaskCompletionServiceApi {

    /**
     * Отмечает задачу выполненной для текущего пользователя.
     *
     * @param taskId идентификатор задачи дорожной карты
     */
    void completeTask(Long taskId);

    /**
     * Снимает отметку выполнения задачи для текущего пользователя.
     *
     * @param taskId идентификатор задачи дорожной карты
     */
    void uncompleteTask(Long taskId);
}

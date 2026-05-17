package com.example.fms.controller;

import com.example.fms.dto.RoadmapResponse;
import org.springframework.http.ResponseEntity;

/**
 * Контракт REST API дорожной карты мигранта.
 */
public interface RoadmapControllerApi {

    /**
     * Возвращает персональную дорожную карту текущего пользователя.
     *
     * @return задачи, выполненные пункты, состояние профиля и сообщение для интерфейса
     */
    ResponseEntity<RoadmapResponse> getRoadmap();

    /**
     * Отмечает пункт дорожной карты выполненным.
     *
     * @param taskId идентификатор задачи
     * @return пустой ответ при успешном сохранении отметки
     */
    ResponseEntity<Void> completeTask(Long taskId);

    /**
     * Снимает отметку выполнения пункта дорожной карты.
     *
     * @param taskId идентификатор задачи
     * @return пустой ответ при успешном удалении отметки
     */
    ResponseEntity<Void> uncompleteTask(Long taskId);
}

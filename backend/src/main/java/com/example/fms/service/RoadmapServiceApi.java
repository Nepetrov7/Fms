package com.example.fms.service;

import com.example.fms.dto.RoadmapResponse;

/**
 * Контракт сервиса формирования дорожной карты текущего пользователя.
 */
public interface RoadmapServiceApi {

    /**
     * Возвращает видимые задачи, статусы выполнения и поясняющее сообщение.
     *
     * @return агрегированный ответ дорожной карты
     */
    RoadmapResponse getRoadmap();
}

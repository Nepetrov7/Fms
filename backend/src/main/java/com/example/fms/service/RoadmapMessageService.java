package com.example.fms.service;

import org.springframework.stereotype.Service;

/**
 * Хранит заранее определенные тексты, которые дорожная карта возвращает пользователю.
 * Сообщения больше не настраиваются через базу данных и админ-панель.
 */
@Service
public class RoadmapMessageService {

    private static final String PROFILE_INCOMPLETE_MESSAGE =
            "Для получения дорожной карты необходимо заполнить профиль";
    private static final String ROADMAP_EMPTY_MESSAGE =
            "Администратор еще не настроил дорожную карту";
    private static final String ROADMAP_COMPLETE_MESSAGE =
            "Все необходимые документы оформлены!";
    private static final String ROADMAP_ACTIVE_MESSAGE =
            "Ваша дорожная карта действий";

    /**
     * Возвращает сообщение для случая, когда профиль еще не заполнен.
     *
     * @return текст с просьбой заполнить профиль
     */
    public String profileIncompleteMessage() {
        return PROFILE_INCOMPLETE_MESSAGE;
    }

    /**
     * Возвращает сообщение для случая, когда в системе нет настроенных задач.
     *
     * @return текст о пустой дорожной карте
     */
    public String emptyRoadmapMessage() {
        return ROADMAP_EMPTY_MESSAGE;
    }

    /**
     * Возвращает сообщение для случая, когда все видимые задачи выполнены.
     *
     * @return текст об успешном завершении дорожной карты
     */
    public String completeRoadmapMessage() {
        return ROADMAP_COMPLETE_MESSAGE;
    }

    /**
     * Возвращает заголовок активной дорожной карты.
     *
     * @return текст заголовка для списка актуальных задач
     */
    public String activeRoadmapMessage() {
        return ROADMAP_ACTIVE_MESSAGE;
    }
}

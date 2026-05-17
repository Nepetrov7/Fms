package com.example.fms.engine;

import com.example.fms.entity.Migrant;
import com.example.fms.entity.User;
import com.example.fms.repository.MigrantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Проверяет, достаточно ли заполнен профиль мигранта для построения дорожной карты.
 *
 * <p>Дорожная карта не выдается, пока в профиле нет базовых данных: имени,
 * фамилии, гражданства, страны прибытия и даты въезда. Этот компонент
 * используется и при выдаче профиля, и перед расчетом видимых задач.</p>
 */
@Component
@RequiredArgsConstructor
public class ProfileCompletenessResolver {

    private final MigrantRepository migrantRepository;

    /**
     * Проверяет заполненность профиля мигранта, связанного с пользователем.
     *
     * @param user авторизованный пользователь
     * @return {@code true}, если профиль найден и содержит все обязательные поля
     */
    public boolean isProfileComplete(User user) {
        return migrantRepository.findByUser_Id(user.getId())
                .map(this::isMigrantProfileComplete)
                .orElse(false);
    }

    /**
     * Проверяет обязательные поля одной сущности мигранта.
     *
     * @param m профиль мигранта из базы данных
     * @return {@code true}, если обязательные текстовые поля заполнены и указана дата въезда
     */
    private boolean isMigrantProfileComplete(Migrant m) {
        return isFilled(m.getFirstName())
                && isFilled(m.getLastName())
                && isFilled(m.getCitizenship())
                && isFilled(m.getCountryOfArrival())
                && m.getArrivalDate() != null;
    }

    /**
     * Проверяет, что строка не равна {@code null} и содержит хотя бы один непробельный символ.
     *
     * @param value проверяемая строка
     * @return {@code true}, если строка заполнена
     */
    private boolean isFilled(String value) {
        return value != null && !value.isBlank();
    }
}

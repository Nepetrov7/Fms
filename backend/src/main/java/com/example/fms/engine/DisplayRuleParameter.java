package com.example.fms.engine;

import com.example.fms.entity.DisplayRule;

/**
 * Параметры профиля мигранта, которые можно использовать в правилах отображения.
 *
 * <p>Имена enum-значений совпадают со строковыми ключами, которые сохраняются
 * в {@link DisplayRule#getParameterKey()} и передаются из админ-панели.</p>
 */
public enum DisplayRuleParameter {
    /** Безусловное правило: задача показывается всем пользователям с заполненным профилем. */
    ALL,

    /** Количество календарных дней, прошедших с даты въезда. */
    DAYS_SINCE_ENTRY,

    /** Признак наличия патента в профиле мигранта. */
    HAS_PATENT,

    /** Признак наличия сертификата владения русским языком. */
    HAS_LANGUAGE_CERTIFICATE,

    /** Страна, из которой прибыл мигрант. */
    COUNTRY_OF_ARRIVAL,

    /** Гражданство мигранта. */
    CITIZENSHIP;

    /**
     * Преобразует строковый ключ правила в параметр профиля.
     *
     * @param key имя параметра из базы данных или API; регистр не важен
     * @return соответствующий параметр правила
     * @throws IllegalArgumentException если ключ пустой или неизвестный
     */
    public static DisplayRuleParameter fromKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Ключ правила не задан");
        }
        return DisplayRuleParameter.valueOf(key.trim().toUpperCase());
    }
}

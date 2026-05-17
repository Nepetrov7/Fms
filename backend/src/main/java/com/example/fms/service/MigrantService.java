package com.example.fms.service;

import com.example.fms.entity.Migrant;
import com.example.fms.repository.MigrantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Небольшая сервисная обертка над сохранением профиля мигранта.
 *
 * <p>Класс оставлен как отдельная точка расширения: если позже при сохранении
 * профиля понадобится дополнительная бизнес-логика, ее можно добавить здесь,
 * не меняя вызывающий код.</p>
 */
@Service
@RequiredArgsConstructor
public class MigrantService {

    private final MigrantRepository migrantRepository;

    /**
     * Сохраняет новый или обновленный профиль мигранта.
     *
     * @param migrant сущность профиля мигранта
     * @return сохраненная сущность с актуальным идентификатором и состоянием
     */
    public Migrant save(Migrant migrant) {
        return migrantRepository.save(migrant);
    }
}

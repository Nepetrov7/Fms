package com.example.fms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Справочник типов патента.
 *
 * <p>Публичный API отдает только активные типы, чтобы устаревшие варианты можно
 * было скрыть без удаления исторических данных.</p>
 */
@Entity
@Table(name = "patent_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Стабильный машинный код, например WORK или SELF_EMPLOYED. */
    @Column(nullable = false, unique = true, length = 64)
    private String code;

    /** Название типа патента для отображения пользователю. */
    @Column(nullable = false, length = 256)
    private String name;

    /** Признак доступности типа в публичном справочнике. */
    @Column(nullable = false)
    private Boolean active = true;
}

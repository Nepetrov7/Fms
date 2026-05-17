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
 * Задача дорожной карты: действие, которое должен выполнить мигрант.
 */
@Entity
@Table(name = "roadmap_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoadmapTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Краткое название действия. */
    @Column(nullable = false)
    private String title;

    /** Название группы, в которой задача отображается на UI. */
    @Column(name = "group_name", length = 256)
    private String groupName;

    /** Подробное описание действия для пользователя. */
    @Column(length = 2000)
    private String description;

    /** Рекомендуемый срок выполнения в днях от даты въезда. */
    @Column(name = "days_to_complete")
    private Integer daysToComplete;

    /** Признак активности задачи в пользовательской дорожной карте. */
    @Column(nullable = false)
    private Boolean active = true;
}

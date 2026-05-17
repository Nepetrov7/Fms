package com.example.fms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Отметка о выполнении задачи дорожной карты конкретным пользователем.
 */
@Entity
@Table(name = "user_task_completions", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "task_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTaskCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Пользователь, который отметил задачу выполненной. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Идентификатор задачи дорожной карты. */
    @Column(name = "task_id", nullable = false)
    private Long taskId;

    /** Время установки отметки выполнения. */
    @Column(name = "completed_at", nullable = false)
    private Instant completedAt;
}

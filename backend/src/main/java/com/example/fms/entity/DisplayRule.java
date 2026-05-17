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
 * Одно условие внутри группы правил отображения задачи дорожной карты.
 *
 * <p>Строки с одинаковыми {@code taskId} и {@code ruleGroupId} объединяются
 * через логическое И. Разные группы одной задачи объединяются через ИЛИ.</p>
 */
@Entity
@Table(name = "task_display_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Идентификатор задачи, к которой относится правило. */
    @Column(name = "task_id", nullable = false)
    private Long taskId;

    /** Номер группы правил внутри одной задачи. */
    @Column(name = "rule_group_id", nullable = false)
    private Integer ruleGroupId;

    /** Ключ проверяемого параметра: ALL, DAYS_SINCE_ENTRY, HAS_PATENT и другие. */
    @Column(name = "parameter_key", nullable = false, length = 64)
    private String parameterKey;

    /** Оператор сравнения: EQ, NE, GT, LT, GTE или LTE. */
    @Column(name = "operator", nullable = false, length = 16)
    private String operator;

    /** Значение условия; колонка не названа value, потому что это зарезервированное слово в SQL/H2. */
    @Column(name = "rule_value", length = 500)
    private String ruleValue;
}

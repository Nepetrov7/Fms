package com.example.fms.repository;

import com.example.fms.entity.DisplayRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий правил отображения задач дорожной карты.
 */
@Repository
public interface DisplayRuleRepository extends JpaRepository<DisplayRule, Long> {

    /** Возвращает все правила задачи, упорядоченные по группе и идентификатору. */
    List<DisplayRule> findByTaskIdOrderByRuleGroupIdAscIdAsc(Long taskId);

    /** Пакетно загружает правила для набора задач дорожной карты. */
    List<DisplayRule> findByTaskIdIn(List<Long> taskIds);

    /** Удаляет все правила задачи. */
    void deleteByTaskId(Long taskId);

    /** Удаляет одну группу правил задачи. */
    void deleteByTaskIdAndRuleGroupId(Long taskId, Integer ruleGroupId);
}

package com.example.fms.repository;

import com.example.fms.entity.RoadmapTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий задач дорожной карты.
 */
@Repository
public interface RoadmapTaskRepository extends JpaRepository<RoadmapTask, Long> {
    /** Возвращает активные задачи для пользовательской дорожной карты. */
    List<RoadmapTask> findAllByActiveTrueOrderByDaysToCompleteAsc();

    /** Возвращает все задачи, включая неактивные, для админки и расчета статусов. */
    List<RoadmapTask> findAllByOrderByDaysToCompleteAsc();

    /**
     * Возвращает уникальные непустые названия групп задач для автодополнения в админке.
     *
     * @return отсортированный список названий групп
     */
    @Query("""
            SELECT DISTINCT t.groupName FROM RoadmapTask t
            WHERE t.groupName IS NOT NULL AND TRIM(t.groupName) <> ''
            ORDER BY t.groupName
            """)
    List<String> findDistinctGroupNamesForAdmin();
}

package com.example.fms.repository;

import com.example.fms.entity.PatentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий справочника типов патента.
 */
@Repository
public interface PatentTypeRepository extends JpaRepository<PatentType, Long> {

    /**
     * Возвращает активные типы патента для публичного справочника.
     *
     * @return типы патента, отсортированные по названию
     */
    List<PatentType> findAllByActiveTrueOrderByNameAsc();
}

package com.example.fms.repository;

import com.example.fms.entity.Migrant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий профилей мигрантов.
 */
@Repository
public interface MigrantRepository extends JpaRepository<Migrant, Long> {

    /**
     * Находит профиль мигранта по идентификатору пользователя.
     *
     * @param userId идентификатор записи из таблицы users
     * @return профиль мигранта, если он был создан
     */
    Optional<Migrant> findByUser_Id(Long userId);
}

package com.example.fms.repository;

import com.example.fms.entity.Patent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий патентов мигрантов.
 */
@Repository
public interface PatentRepository extends JpaRepository<Patent, Long> {
}

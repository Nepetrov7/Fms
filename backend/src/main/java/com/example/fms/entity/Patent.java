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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Данные патента мигранта: тип, номер, название и сроки действия.
 */
@Entity
@Table(name = "patents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Тип патента из справочника. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patent_type_id")
    private PatentType patentType;

    /** Номер патента, обязательный при указании наличия патента в профиле. */
    @Column(name = "patent_number", nullable = false)
    private String patentNumber;

    /** Произвольное название или описание патента. */
    @Column(name = "title", length = 1000)
    private String title;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;
}

package com.example.fms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Профиль мигранта, связанный с одной учетной записью пользователя.
 *
 * <p>Содержит персональные данные, сведения о въезде, признаки для правил
 * дорожной карты и опциональную связь с патентом.</p>
 */
@Entity
@Table(name = "migrants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Migrant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "citizenship")
    private String citizenship;

    @Column(name = "country_of_arrival")
    private String countryOfArrival;

    /** Цель визита в свободной текстовой форме. */
    @Column(name = "visit_purpose", length = 1000)
    private String visitPurpose;

    /** Планируемая длительность пребывания в днях. */
    @Column(name = "visit_duration_days")
    private Integer visitDurationDays;

    /** Дата въезда, от которой рассчитываются сроки задач. */
    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    /** Есть ли сертификат владения русским языком. */
    @Column(name = "has_language_certificate")
    private Boolean hasLanguageCertificate;

    /** Есть ли патент; может дублировать наличие связанной сущности {@link #patent}. */
    @Column(name = "has_patent")
    private Boolean hasPatent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patent_id")
    private Patent patent;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}

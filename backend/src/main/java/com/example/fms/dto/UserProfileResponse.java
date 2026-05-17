package com.example.fms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Профиль мигранта и краткие данные учетной записи для отображения на клиенте.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String login;
    private String firstName;
    private String lastName;
    private String middleName;
    private String citizenship;
    private String countryOfArrival;
    private String visitPurpose;
    private Integer visitDurationDays;
    private LocalDate arrivalDate;
    private Boolean hasLanguageCertificate;
    private Boolean hasPatent;
    private Long patentId;
    private Long patentTypeId;
    private String patentTypeName;
    private String patentNumber;
    private String patentTitle;
    private LocalDate patentIssueDate;
    private LocalDate patentExpiryDate;
    private Boolean isProfileComplete;
}

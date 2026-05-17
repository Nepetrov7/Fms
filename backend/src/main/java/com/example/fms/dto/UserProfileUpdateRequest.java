package com.example.fms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Запрос на обновление профиля мигранта, включая необязательные данные патента.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequest {
    @NotBlank(message = "Имя обязательно")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    private String lastName;

    private String middleName;

    @NotBlank(message = "Гражданство обязательно")
    private String citizenship;

    @NotBlank(message = "Страна прибытия обязательна")
    private String countryOfArrival;

    private String visitPurpose;

    private Integer visitDurationDays;

    @NotNull(message = "Дата прибытия обязательна")
    private LocalDate arrivalDate;

    private Boolean hasLanguageCertificate;

    private Boolean hasPatent;

    private Long patentTypeId;
    private String patentNumber;
    private String patentTitle;
    private LocalDate patentIssueDate;
    private LocalDate patentExpiryDate;
}

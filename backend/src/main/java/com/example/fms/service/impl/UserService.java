package com.example.fms.service.impl;

import com.example.fms.dto.UserProfileResponse;
import com.example.fms.dto.UserProfileUpdateRequest;
import com.example.fms.engine.ProfileCompletenessResolver;
import com.example.fms.entity.Migrant;
import com.example.fms.entity.Patent;
import com.example.fms.entity.User;
import com.example.fms.repository.MigrantRepository;
import com.example.fms.repository.PatentRepository;
import com.example.fms.repository.PatentTypeRepository;
import com.example.fms.repository.UserRepository;
import com.example.fms.service.UserServiceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Работает с учетной записью и профилем мигранта текущего пользователя.
 *
 * <p>Сервис берет пользователя из {@code SecurityContext}, загружает актуальную
 * запись из базы данных и синхронизирует связанные данные профиля, включая
 * сведения о патенте.</p>
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserServiceApi {

    private final UserRepository userRepository;
    private final MigrantRepository migrantRepository;
    private final PatentRepository patentRepository;
    private final PatentTypeRepository patentTypeRepository;
    private final ProfileCompletenessResolver profileCompletenessResolver;

    /**
     * Возвращает текущего пользователя из контекста безопасности.
     *
     * @return актуальная сущность пользователя из базы данных
     * @throws IllegalStateException если пользователь не авторизован или запись уже отсутствует в базе
     */
    @Override
    public User getCurrentUser() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            throw new IllegalStateException("Пользователь не авторизован");
        }
        User principal = (User) auth.getPrincipal();
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));
    }

    /**
     * Возвращает профиль текущего пользователя.
     *
     * @return DTO профиля с данными мигранта, патента и признаком заполненности
     */
    @Override
    public UserProfileResponse getProfile() {
        User user = getCurrentUser();
        Migrant migrant = migrantRepository.findByUser_Id(user.getId()).orElse(null);
        return mapToProfileResponse(user, migrant);
    }

    /**
     * Обновляет профиль мигранта и связанные данные патента.
     *
     * <p>Если пользователь указал, что патент есть, сервис требует тип и номер
     * патента, создает или обновляет связанную запись {@link Patent}. Если патента
     * нет, связь с патентом очищается.</p>
     *
     * @param request новые данные профиля
     * @return обновленный профиль
     * @throws IllegalArgumentException если данные патента неполные или тип патента не найден
     */
    @Override
    @Transactional
    public UserProfileResponse updateProfile(UserProfileUpdateRequest request) {
        User user = getCurrentUser();
        Migrant migrant = migrantRepository.findByUser_Id(user.getId()).orElseGet(() -> {
            Migrant nm = new Migrant();
            nm.setUser(user);
            return nm;
        });

        migrant.setFirstName(request.getFirstName());
        migrant.setLastName(request.getLastName());
        migrant.setMiddleName(request.getMiddleName());
        migrant.setCitizenship(request.getCitizenship());
        migrant.setCountryOfArrival(request.getCountryOfArrival());
        migrant.setVisitPurpose(request.getVisitPurpose());
        migrant.setVisitDurationDays(request.getVisitDurationDays());
        migrant.setArrivalDate(request.getArrivalDate());
        migrant.setHasLanguageCertificate(request.getHasLanguageCertificate());

        boolean hasPatent = Boolean.TRUE.equals(request.getHasPatent());
        migrant.setHasPatent(hasPatent);

        if (hasPatent) {
            if (request.getPatentTypeId() == null) {
                throw new IllegalArgumentException("Выберите тип патента");
            }
            if (request.getPatentNumber() == null || request.getPatentNumber().isBlank()) {
                throw new IllegalArgumentException("Укажите номер патента");
            }
            Patent patent = migrant.getPatent();
            if (patent == null) {
                patent = new Patent();
            }
            patent.setPatentType(
                    patentTypeRepository.findById(request.getPatentTypeId())
                            .orElseThrow(() -> new IllegalArgumentException("Тип патента не найден")));
            patent.setPatentNumber(request.getPatentNumber().trim());
            patent.setTitle(request.getPatentTitle());
            patent.setIssueDate(request.getPatentIssueDate());
            patent.setExpiryDate(request.getPatentExpiryDate());
            patent = patentRepository.save(patent);
            migrant.setPatent(patent);
        } else {
            migrant.setPatent(null);
        }

        migrant = migrantRepository.save(migrant);
        return mapToProfileResponse(user, migrant);
    }

    /**
     * Собирает DTO профиля из пользователя и связанной записи мигранта.
     *
     * @param user учетная запись пользователя
     * @param migrant профиль мигранта; может быть {@code null}
     * @return DTO, готовый для ответа API
     */
    private UserProfileResponse mapToProfileResponse(User user, Migrant migrant) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setLogin(user.getLogin());
        if (migrant != null) {
            response.setFirstName(migrant.getFirstName());
            response.setLastName(migrant.getLastName());
            response.setMiddleName(migrant.getMiddleName());
            response.setCitizenship(migrant.getCitizenship());
            response.setCountryOfArrival(migrant.getCountryOfArrival());
            response.setVisitPurpose(migrant.getVisitPurpose());
            response.setVisitDurationDays(migrant.getVisitDurationDays());
            response.setArrivalDate(migrant.getArrivalDate());
            response.setHasLanguageCertificate(migrant.getHasLanguageCertificate());
            response.setHasPatent(Boolean.TRUE.equals(migrant.getHasPatent())
                    || migrant.getPatent() != null);
            if (migrant.getPatent() != null) {
                Patent p = migrant.getPatent();
                response.setPatentId(p.getId());
                response.setPatentNumber(p.getPatentNumber());
                response.setPatentTitle(p.getTitle());
                response.setPatentIssueDate(p.getIssueDate());
                response.setPatentExpiryDate(p.getExpiryDate());
                if (p.getPatentType() != null) {
                    response.setPatentTypeId(p.getPatentType().getId());
                    response.setPatentTypeName(p.getPatentType().getName());
                }
            }
        }
        response.setIsProfileComplete(profileCompletenessResolver.isProfileComplete(user));
        return response;
    }
}

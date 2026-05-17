package com.example.fms.service.admin;

import com.example.fms.dto.admin.DisplayRuleConditionDto;
import com.example.fms.dto.admin.DisplayRuleConditionRequest;
import com.example.fms.dto.admin.DisplayRuleGroupDto;
import com.example.fms.dto.admin.DisplayRuleGroupRequest;
import com.example.fms.entity.DisplayRule;
import com.example.fms.repository.DisplayRuleRepository;
import com.example.fms.repository.RoadmapTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Управляет группами правил отображения задач в админ-панели.
 *
 * <p>Каждая группа правил хранится как несколько строк {@link DisplayRule}
 * с одинаковым {@code taskId} и {@code ruleGroupId}. При обновлении группа
 * полностью пересоздается, чтобы не оставлять устаревшие условия.</p>
 */
@Service
@RequiredArgsConstructor
public class AdminDisplayRuleService {

    private final DisplayRuleRepository displayRuleRepository;
    private final RoadmapTaskRepository taskRepository;

    /**
     * Возвращает все группы правил для указанной задачи.
     *
     * @param taskId идентификатор задачи дорожной карты
     * @return список групп с условиями, отсортированный по номеру группы
     */
    public List<DisplayRuleGroupDto> listGroupsByTask(Long taskId) {
        ensureTaskExists(taskId);
        Map<Integer, List<DisplayRule>> grouped = displayRuleRepository.findByTaskIdOrderByRuleGroupIdAscIdAsc(taskId)
                .stream()
                .collect(Collectors.groupingBy(DisplayRule::getRuleGroupId, LinkedHashMap::new, Collectors.toList()));
        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> toGroupDto(taskId, e.getKey(), e.getValue()))
                .toList();
    }

    /**
     * Создает новую группу правил для задачи.
     *
     * @param taskId идентификатор задачи дорожной карты
     * @param request набор условий новой группы
     * @return созданная группа правил
     */
    @Transactional
    public DisplayRuleGroupDto createGroup(Long taskId, DisplayRuleGroupRequest request) {
        ensureTaskExists(taskId);
        validateConditions(request.getConditions());
        int groupId = nextGroupId(taskId);
        List<DisplayRule> saved = saveConditions(taskId, groupId, request.getConditions());
        return toGroupDto(taskId, groupId, saved);
    }

    /**
     * Полностью заменяет условия существующей группы правил.
     *
     * @param taskId идентификатор задачи дорожной карты
     * @param groupId номер группы внутри задачи
     * @param request новый набор условий
     * @return обновленная группа правил
     */
    @Transactional
    public DisplayRuleGroupDto updateGroup(Long taskId, Integer groupId, DisplayRuleGroupRequest request) {
        ensureTaskExists(taskId);
        validateConditions(request.getConditions());
        displayRuleRepository.deleteByTaskIdAndRuleGroupId(taskId, groupId);
        List<DisplayRule> saved = saveConditions(taskId, groupId, request.getConditions());
        return toGroupDto(taskId, groupId, saved);
    }

    /**
     * Удаляет одну группу правил задачи.
     *
     * @param taskId идентификатор задачи дорожной карты
     * @param groupId номер группы внутри задачи
     */
    @Transactional
    public void deleteGroup(Long taskId, Integer groupId) {
        ensureTaskExists(taskId);
        displayRuleRepository.deleteByTaskIdAndRuleGroupId(taskId, groupId);
    }

    /**
     * Сохраняет условия группы как отдельные строки правил.
     *
     * @param taskId идентификатор задачи дорожной карты
     * @param groupId номер группы правил
     * @param conditions условия, пришедшие из админ-панели
     * @return сохраненные сущности правил
     */
    private List<DisplayRule> saveConditions(Long taskId, int groupId, List<DisplayRuleConditionRequest> conditions) {
        List<DisplayRule> saved = new ArrayList<>();
        for (DisplayRuleConditionRequest c : conditions) {
            DisplayRule rule = new DisplayRule();
            rule.setTaskId(taskId);
            rule.setRuleGroupId(groupId);
            rule.setParameterKey(c.getParameterKey().trim().toUpperCase());
            rule.setOperator(c.getOperator() != null ? c.getOperator().trim().toUpperCase() : "EQ");
            rule.setRuleValue(c.getValue());
            saved.add(displayRuleRepository.save(rule));
        }
        return saved;
    }

    /**
     * Вычисляет следующий свободный номер группы правил для задачи.
     *
     * @param taskId идентификатор задачи дорожной карты
     * @return максимальный существующий {@code ruleGroupId} плюс один
     */
    private int nextGroupId(Long taskId) {
        return displayRuleRepository.findByTaskIdOrderByRuleGroupIdAscIdAsc(taskId).stream()
                .map(DisplayRule::getRuleGroupId)
                .max(Comparator.naturalOrder())
                .orElse(0) + 1;
    }

    /**
     * Проверяет, что группа содержит хотя бы одно условие.
     *
     * @param conditions условия группы
     * @throws IllegalArgumentException если список условий пустой
     */
    private void validateConditions(List<DisplayRuleConditionRequest> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            throw new IllegalArgumentException("Добавьте хотя бы одно условие");
        }
    }

    /**
     * Проверяет существование задачи перед изменением ее правил.
     *
     * @param taskId идентификатор задачи дорожной карты
     * @throws RuntimeException если задача не найдена
     */
    private void ensureTaskExists(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new RuntimeException("Задача не найдена: " + taskId);
        }
    }

    /**
     * Преобразует набор сущностей правил в DTO группы для админского API.
     *
     * @param taskId идентификатор задачи дорожной карты
     * @param groupId номер группы правил
     * @param rules сохраненные правила группы
     * @return DTO группы правил
     */
    private DisplayRuleGroupDto toGroupDto(Long taskId, Integer groupId, List<DisplayRule> rules) {
        List<DisplayRuleConditionDto> conditions = rules.stream()
                .map(r -> new DisplayRuleConditionDto(r.getId(), r.getParameterKey(), r.getOperator(), r.getRuleValue()))
                .toList();
        return new DisplayRuleGroupDto(groupId, taskId, conditions);
    }
}

package com.example.fms.controller.admin;

import com.example.fms.dto.admin.DisplayRuleGroupDto;
import com.example.fms.dto.admin.DisplayRuleGroupRequest;
import com.example.fms.service.admin.AdminDisplayRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API управления группами правил отображения задач дорожной карты.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminDisplayRuleController {

    private final AdminDisplayRuleService displayRuleService;

    /**
     * Возвращает все группы правил для задачи.
     *
     * @param taskId идентификатор задачи
     * @return список групп правил или 404, если задача не найдена
     */
    @GetMapping("/tasks/{taskId}/display-rules")
    public ResponseEntity<List<DisplayRuleGroupDto>> list(@PathVariable Long taskId) {
        try {
            return ResponseEntity.ok(displayRuleService.listGroupsByTask(taskId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Создает новую группу правил для задачи.
     *
     * @param taskId идентификатор задачи
     * @param request условия новой группы
     * @return созданная группа, 400 при ошибке валидации или 404 при неизвестной задаче
     */
    @PostMapping("/tasks/{taskId}/display-rules")
    public ResponseEntity<DisplayRuleGroupDto> create(
            @PathVariable Long taskId,
            @RequestBody DisplayRuleGroupRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(displayRuleService.createGroup(taskId, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Заменяет условия существующей группы правил.
     *
     * @param taskId идентификатор задачи
     * @param groupId номер группы правил
     * @param request новый набор условий
     * @return обновленная группа, 400 при ошибке валидации или 404 при неизвестной задаче
     */
    @PutMapping("/tasks/{taskId}/display-rules/{groupId}")
    public ResponseEntity<DisplayRuleGroupDto> update(
            @PathVariable Long taskId,
            @PathVariable Integer groupId,
            @RequestBody DisplayRuleGroupRequest request) {
        try {
            return ResponseEntity.ok(displayRuleService.updateGroup(taskId, groupId, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Удаляет группу правил задачи.
     *
     * @param taskId идентификатор задачи
     * @param groupId номер группы правил
     * @return 204 при успешном удалении или 404 при неизвестной задаче
     */
    @DeleteMapping("/tasks/{taskId}/display-rules/{groupId}")
    public ResponseEntity<Void> delete(@PathVariable Long taskId, @PathVariable Integer groupId) {
        try {
            displayRuleService.deleteGroup(taskId, groupId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

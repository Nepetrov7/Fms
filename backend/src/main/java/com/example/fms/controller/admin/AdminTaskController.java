package com.example.fms.controller.admin;

import com.example.fms.dto.admin.TaskDto;
import com.example.fms.service.admin.AdminTaskCommandService;
import com.example.fms.service.admin.AdminTaskQueryService;
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
import java.util.Map;

/**
 * REST API управления задачами дорожной карты в админ-панели.
 */
@RestController
@RequestMapping("/api/admin/tasks")
@CrossOrigin(origins = "*")
public class AdminTaskController {

    private final AdminTaskQueryService taskQueryService;
    private final AdminTaskCommandService taskCommandService;

    /**
     * Создает контроллер задач админ-панели.
     *
     * @param taskQueryService сервис чтения задач
     * @param taskCommandService сервис изменения задач
     */
    public AdminTaskController(AdminTaskQueryService taskQueryService, AdminTaskCommandService taskCommandService) {
        this.taskQueryService = taskQueryService;
        this.taskCommandService = taskCommandService;
    }

    /**
     * Возвращает все задачи дорожной карты.
     *
     * @return список задач, включая неактивные
     */
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        return ResponseEntity.ok(taskQueryService.getAllTasks());
    }

    /**
     * Возвращает существующие названия групп задач.
     *
     * @return список названий групп
     */
    @GetMapping("/groups")
    public ResponseEntity<List<String>> getGroupNames() {
        return ResponseEntity.ok(taskQueryService.getDistinctGroupNames());
    }

    /**
     * Возвращает задачу по идентификатору.
     *
     * @param id идентификатор задачи
     * @return задача или 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(taskQueryService.getTask(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Создает новую задачу дорожной карты.
     *
     * @param body поля {@code title}, {@code groupName}, {@code description}, {@code daysToComplete}
     * @return созданная задача со статусом 201
     */
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody Map<String, Object> body) {
        TaskDto created = taskCommandService.createTask(
                (String) body.get("title"),
                (String) body.get("groupName"),
                (String) body.get("description"),
                intOrNull(body.get("daysToComplete")));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Частично обновляет существующую задачу.
     *
     * @param id идентификатор задачи
     * @param body поля для изменения, включая {@code active}
     * @return обновленная задача или 404
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            return ResponseEntity.ok(taskCommandService.updateTask(id,
                    (String) body.get("title"),
                    (String) body.get("groupName"),
                    (String) body.get("description"),
                    intOrNull(body.get("daysToComplete")),
                    body.get("active") instanceof Boolean b ? b : null));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Удаляет задачу и связанные с ней правила отображения.
     *
     * @param id идентификатор задачи
     * @return 204 при успешном удалении или 404
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        try {
            taskCommandService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Преобразует произвольное значение из JSON-тела в {@link Integer}.
     *
     * @param v значение из тела запроса
     * @return число или {@code null}, если значение отсутствует
     */
    private static Integer intOrNull(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Number n) {
            return n.intValue();
        }
        return Integer.parseInt(v.toString());
    }
}

package de.tomwey2.taskappbackend.controller;

import de.tomwey2.taskappbackend.model.Task;
import de.tomwey2.taskappbackend.model.TaskDto;
import de.tomwey2.taskappbackend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api") // Basis-URL für alle Endpunkte in diesem Controller
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // GET /api/tasks -> Alle Tasks abrufen
    @GetMapping("/tasks")
    public List<TaskDto> getAllTasks() {
        return taskService.getAllTasks();
    }

    // GET /api/tasks/{id} -> Einen Task anhand seiner ID abrufen
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok) // Kurzform für .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/users/{userId}/tasks -> Erstellt einen neuen Task für einen bestimmten User
    @PostMapping("/users/{userId}/tasks")
    public ResponseEntity<TaskDto> createTask(
            @PathVariable Long userId,
            @RequestBody Task task) {

        TaskDto createdTask = taskService.createTask(task, userId);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    // PUT /api/tasks/{id} -> Einen bestehenden Task aktualisieren
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @RequestBody Task task) {
        return taskService.updateTask(id, task)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/tasks/{id} -> Einen Task löschen
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskService.deleteTask(id)) {
            return ResponseEntity.noContent().build(); // Status 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // Status 404 Not Found
        }
    }
}
package de.tomwey2.taskappbackend.controller;

import de.tomwey2.taskappbackend.model.Task;
import de.tomwey2.taskappbackend.dto.TaskRequestDto;
import de.tomwey2.taskappbackend.dto.TaskResponseDto;
import de.tomwey2.taskappbackend.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api") // Basis-URL für alle Endpunkte in diesem Controller
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // GET /api/tasks -> Alle Tasks abrufen
    @GetMapping("/tasks")
    public List<TaskResponseDto> findTasks(
            @RequestParam(name = "projectId", required = false) Long projectId,
            @RequestParam(name = "assignedToUserId", required = false) Long assignedToUserId) {

        // Wenn beide Parameter null sind, funktioniert dies wie eine "findAll"-Abfrage.
        // Andernfalls wird gefiltert.
        return taskService.searchTasks(projectId, assignedToUserId);
    }

    // GET /api/tasks/{id} -> Einen Task anhand seiner ID abrufen
    @GetMapping("/tasks/{id}")
    public ResponseEntity<EntityModel<TaskResponseDto>>  getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(taskResponseDto -> {
                    EntityModel<TaskResponseDto> taskModel = EntityModel.of(taskResponseDto);
                    taskModel.add(linkTo(methodOn(TaskController.class).getTaskById(id)).withSelfRel());
                    taskModel.add(linkTo(methodOn(TaskController.class).findTasks(null, null)).withRel("tasks"));
                    // Optional: Erstelle einen Link auf den zugehörigen reportedBy User
                    if (taskResponseDto.reportedBy() != null) {
                        taskModel.add(linkTo(methodOn(UserController.class).getUserById(taskResponseDto.reportedBy().id())).withRel("reportedBy"));
                    }
                    // Optional: Erstelle einen Link auf den zugehörigen assignedTo User
                    if (taskResponseDto.assignedTo() != null) {
                        taskModel.add(linkTo(methodOn(UserController.class).getUserById(taskResponseDto.assignedTo().id())).withRel("assignedTo"));
                    }
                    // Optional: Erstelle einen Link auf das zugehörige Projekt
                    if (taskResponseDto.belongsTo() != null) {
                        taskModel.add(linkTo(methodOn(ProjectController.class).getProjectById(taskResponseDto.belongsTo().id())).withRel("project"));
                    }
                    return taskModel;
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

        //return taskService.getTaskById(id)
        //        .map(ResponseEntity::ok) // Kurzform für .map(task -> ResponseEntity.ok(task))
        //        .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/users/{userId}/tasks -> Erstellt einen neuen Task. Der User userId ist der Reporter
    @PostMapping("/users/{userId}/tasks")
    public ResponseEntity<TaskResponseDto> createTask(
            @PathVariable Long userId,
            @Valid @RequestBody TaskRequestDto taskRequest) {

        TaskResponseDto createdTask = taskService.createTask(taskRequest, userId);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    // PUT /api/tasks/{id} -> Einen bestehenden Task aktualisieren
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id, @RequestBody TaskRequestDto taskRequest) {
        return taskService.updateTask(id, taskRequest)
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
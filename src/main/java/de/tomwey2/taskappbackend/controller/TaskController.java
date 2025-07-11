package de.tomwey2.taskappbackend.controller;

import de.tomwey2.taskappbackend.dto.TaskRequestDto;
import de.tomwey2.taskappbackend.dto.TaskResponseDto;
import de.tomwey2.taskappbackend.model.Task;
import de.tomwey2.taskappbackend.dto.TaskModelAssembler;
import de.tomwey2.taskappbackend.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api") // Basis-URL für alle Endpunkte in diesem Controller
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Operations that affect tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskModelAssembler taskModelAssembler;

    @Operation(
            summary = "Search for tasks",
            description = "Retrieves a list of tasks, optionally filtered by project id and assigned user." +
                    "If no parameter is specified, all tasks are returned.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    })
    @GetMapping("/tasks")
    public CollectionModel<EntityModel<TaskResponseDto>> findTasks(
            @Parameter(description = "ID of the project to filter by")  // Swagger-UI
            @RequestParam(name = "projectId", required = false) Long projectId,
            @Parameter(description = "ID of the user to whom the tasks are assigned")
            @RequestParam(name = "assignedToUserId", required = false) Long assignedToUserId,
            @Parameter(description = "A substring of the title")
            @RequestParam(name = "title", required = false) String title) {

        // Wenn beide Parameter null sind, funktioniert dies wie eine "findAll"-Abfrage.
        // Andernfalls wird gefiltert.
        List<Task> tasks = taskService.searchTasks(projectId, assignedToUserId, title);

        // Der Assembler bietet auch eine Methode, um eine ganze Collection zu konvertieren.
        // Wir fügen noch den Self-Link für die Collection hinzu.
        return taskModelAssembler.toCollectionModel(tasks)
                .add(linkTo(methodOn(TaskController.class).findTasks(projectId, assignedToUserId, title)).withSelfRel());
    }

    @Operation(
            summary = "Get a task with a given id",
            description = "Retrieves a task with a given task id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Task with id Not Found", content = @Content)
    })
    @GetMapping("/tasks/{id}")
    public ResponseEntity<EntityModel<TaskResponseDto>> getTaskById(
            @Parameter(description = "Id of the task")  // Swagger-UI
            @PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(taskModelAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create a new task for a given project",
            description = "Creates a new task. The user id is the reporter of the task.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "404", description = "User with id Not Found"),
    })
    @PostMapping("/projects/{projectId}/users/{userId}/tasks")
    public ResponseEntity<EntityModel<TaskResponseDto>> createTask(
            @Parameter(description = "id of the project")  // Swagger-UI
            @PathVariable Long projectId,
            @Parameter(description = "id of the user")  // Swagger-UI
            @PathVariable Long userId,
            @Valid @RequestBody TaskRequestDto taskRequest) {

        Task task = taskService.createTask(taskRequest, projectId, userId);
        return new ResponseEntity<>(taskModelAssembler.toModel(task), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update the task with a given id",
            description = "Updates the task with a given id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Task with id Not Found"),
    })
    @PutMapping("/tasks/{id}")
    public ResponseEntity<EntityModel<TaskResponseDto>> updateTask(
            @Parameter(description = "id of the task")  // Swagger-UI
            @PathVariable Long id,
            @RequestBody TaskRequestDto taskRequest) {
        return taskService.updateTask(id, taskRequest)
                .map(taskModelAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/tasks/{id} -> Einen Task löschen
    @Operation(
            summary = "Delete the task with a given id",
            description = "Deletes the task with a given id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Task with id Not Found"),
    })
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Id of the task")  // Swagger-UI
            @PathVariable Long id) {
        if (taskService.deleteTask(id)) {
            return ResponseEntity.noContent().build(); // Status 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // Status 404 Not Found
        }
    }
}
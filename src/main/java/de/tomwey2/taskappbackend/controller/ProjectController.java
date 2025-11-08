package de.tomwey2.taskappbackend.controller;

import de.tomwey2.taskappbackend.dto.*;
import de.tomwey2.taskappbackend.model.Project;
import de.tomwey2.taskappbackend.model.Task;
import de.tomwey2.taskappbackend.service.ProjectService;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Operations that affect projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectModelAssembler projectModelAssembler;
    private final TaskService taskService;
    private final TaskModelAssembler taskModelAssembler;

    @Operation(
            summary = "Create a new project",
            description = "Creates a new project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "404", description = "Project with this name already exists."),
    })
    @PostMapping("/projects")
    public ResponseEntity<EntityModel<ProjectResponseDto>> createProject(@RequestBody ProjectRequestDto projectRequest) {
        Project project = projectService.createProject(projectRequest);
        return new ResponseEntity<>(projectModelAssembler.toModel(project), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all projects or (optional) get project with name",
            description = "Retrieves a list of projects, optionally filtered by project name." +
                    "If no parameter is specified, all projects are returned.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    })
    @GetMapping("/projects")
    public CollectionModel<EntityModel<ProjectResponseDto>> findProjects(
            @Parameter(description = "A substring of the project name")  // Swagger-UI
            @RequestParam(name = "name", required = false) String name) {
        List<Project> projects = projectService.searchProjects(name);
        return projectModelAssembler.toCollectionModel(projects)
                .add(linkTo(methodOn(ProjectController.class).findProjects(name)).withSelfRel());
    }

    @Operation(
            summary = "Get a project with a given id",
            description = "Retrieves a project with a given task id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Project with id Not Found", content = @Content)
    })
    @GetMapping("/projects/{id}")
    public ResponseEntity<EntityModel<ProjectResponseDto>> getProjectById(
            @Parameter(description = "ID of the project")  // Swagger-UI
            @PathVariable Long id) {
        return projectService.getProjectById(id)
                .map(projectModelAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Get all tasks for a specific project",
            description = "Retrieves a list of tasks for a specific project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Project with id Not Found", content = @Content)
    })
    @GetMapping("/projects/{projectId}/tasks")
    public CollectionModel<EntityModel<TaskResponseDto>> findTasksOfProject(
            @Parameter(description = "ID of the project")  // Swagger-UI
            @PathVariable(name = "projectId") Long projectId,
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
            summary = "Create a new task for a given project",
            description = "Creates a new task. The user id is the reporter of the task.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "404", description = "User with id Not Found"),
    })
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<EntityModel<TaskResponseDto>> createTask(
            @Parameter(description = "id of the project")  // Swagger-UI
            @PathVariable Long projectId,
            @Valid @RequestBody TaskRequestDto taskRequest) {

        Task task = taskService.createTask(taskRequest, projectId);
        return new ResponseEntity<>(taskModelAssembler.toModel(task), HttpStatus.CREATED);
    }

}
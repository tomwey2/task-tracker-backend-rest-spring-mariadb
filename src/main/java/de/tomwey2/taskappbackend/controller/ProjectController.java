package de.tomwey2.taskappbackend.controller;

import de.tomwey2.taskappbackend.dto.ProjectModelAssembler;
import de.tomwey2.taskappbackend.dto.ProjectRequestDto;
import de.tomwey2.taskappbackend.dto.ProjectResponseDto;
import de.tomwey2.taskappbackend.model.Project;
import de.tomwey2.taskappbackend.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public ResponseEntity<EntityModel<ProjectResponseDto>> getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id)
                .map(projectModelAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
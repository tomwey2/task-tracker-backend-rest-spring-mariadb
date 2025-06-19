package de.tomwey2.taskappbackend.controller;

import de.tomwey2.taskappbackend.dto.ProjectModelAssembler;
import de.tomwey2.taskappbackend.dto.ProjectRequestDto;
import de.tomwey2.taskappbackend.dto.ProjectResponseDto;
import de.tomwey2.taskappbackend.model.Project;
import de.tomwey2.taskappbackend.service.ProjectService;
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
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectModelAssembler projectModelAssembler;

    @PostMapping
    public ResponseEntity<EntityModel<ProjectResponseDto>> createUser(@RequestBody ProjectRequestDto projectRequest) {
        Project project = projectService.createProject(projectRequest);
        return new ResponseEntity<>(projectModelAssembler.toModel(project), HttpStatus.CREATED);
    }

    @GetMapping
    public CollectionModel<EntityModel<ProjectResponseDto>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        // Der Assembler bietet auch eine Methode, um eine ganze Collection zu konvertieren.
        // Wir fügen noch den Self-Link für die Collection hinzu.
        return projectModelAssembler.toCollectionModel(projects)
                .add(linkTo(methodOn(ProjectController.class).getAllProjects()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProjectResponseDto>> getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id)
                .map(projectModelAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
package de.tomwey2.taskappbackend.dto;

import de.tomwey2.taskappbackend.controller.ProjectController;
import de.tomwey2.taskappbackend.model.Project;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component // Damit Spring diese Klasse als Bean erkennt und wir sie injecten können
public class ProjectModelAssembler implements RepresentationModelAssembler<Project, EntityModel<ProjectResponseDto>> {

    @Override
    public EntityModel<ProjectResponseDto> toModel(Project project) {
        // 1. Konvertiere die Entität in das DTO
        ProjectResponseDto projectDto = new ProjectResponseDto(
                project.getId(),
                project.getName(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );

        // 2. Erstelle das EntityModel aus dem DTO
        EntityModel<ProjectResponseDto> projectModel = EntityModel.of(projectDto);

        // 3. Füge die Links hinzu, die auf den Daten der originalen 'project'-Entität basieren
        projectModel.add(linkTo(methodOn(ProjectController.class).getProjectById(project.getId())).withSelfRel());
        return projectModel;
    }
}

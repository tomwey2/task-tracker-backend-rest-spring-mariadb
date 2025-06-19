package de.tomwey2.taskappbackend.model;

import de.tomwey2.taskappbackend.controller.ProjectController;
import de.tomwey2.taskappbackend.controller.TaskController;
import de.tomwey2.taskappbackend.controller.UserController; // Annahme: Du hast einen UserController
import de.tomwey2.taskappbackend.dto.TaskResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component // Damit Spring diese Klasse als Bean erkennt und wir sie injecten können
public class TaskModelAssembler implements RepresentationModelAssembler<Task, EntityModel<TaskResponseDto>> {

    @Override
    public EntityModel<TaskResponseDto> toModel(Task task) {
        // 1. Konvertiere die Entität in das DTO
        TaskResponseDto taskDto = new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getState(),
                task.getDeadline(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );

        // 2. Erstelle das EntityModel aus dem DTO
        EntityModel<TaskResponseDto> taskModel = EntityModel.of(taskDto);

        // 3. Füge die Links hinzu, die auf den Daten der originalen 'task'-Entität basieren
        taskModel.add(linkTo(methodOn(TaskController.class).getTaskById(task.getId())).withSelfRel());
        taskModel.add(linkTo(methodOn(TaskController.class).findTasks(null, null)).withRel("tasks"));

        // Optional: Erstelle einen Link auf den zugehörigen reportedBy User
        if (task.getReportedBy() != null) {
            taskModel.add(linkTo(methodOn(UserController.class).getUserById(task.getReportedBy().getId())).withRel("reportedBy"));
        }
        // Optional: Erstelle einen Link auf den zugehörigen assignedTo User
        if (task.getAssignedTo() != null) {
            taskModel.add(linkTo(methodOn(UserController.class).getUserById(task.getAssignedTo().getId())).withRel("assignedTo"));
        }
        // Optional: Erstelle einen Link auf das zugehörige Projekt
        if (task.getBelongsTo() != null) {
            taskModel.add(linkTo(methodOn(ProjectController.class).getProjectById(task.getBelongsTo().getId())).withRel("project"));
        }

        return taskModel;
    }
}
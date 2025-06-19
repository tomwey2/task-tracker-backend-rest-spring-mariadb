package de.tomwey2.taskappbackend.dto;

import de.tomwey2.taskappbackend.controller.ProjectController;
import de.tomwey2.taskappbackend.model.User;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component // Damit Spring diese Klasse als Bean erkennt und wir sie injecten können
public class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<UserResponseDto>> {

    @Override
    public EntityModel<UserResponseDto> toModel(User user) {
        // 1. Konvertiere die Entität in das DTO
        UserResponseDto userDto = new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );

        // 2. Erstelle das EntityModel aus dem DTO
        EntityModel<UserResponseDto> userModel = EntityModel.of(userDto);

        // 3. Füge die Links hinzu, die auf den Daten der originalen 'user'-Entität basieren
        userModel.add(linkTo(methodOn(ProjectController.class).getProjectById(user.getId())).withSelfRel());
        return userModel;
    }
}

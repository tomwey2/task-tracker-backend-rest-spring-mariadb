package de.tomwey2.taskappbackend.dto;

import de.tomwey2.taskappbackend.controller.CommentController;
import de.tomwey2.taskappbackend.controller.TaskController;
import de.tomwey2.taskappbackend.controller.UserController;
import de.tomwey2.taskappbackend.model.Comment;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CommentModelAssembler implements RepresentationModelAssembler<Comment, EntityModel<CommentResponseDto>> {

    @Override
    public EntityModel<CommentResponseDto> toModel(Comment comment) {
        CommentResponseDto dto = new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );

        EntityModel<CommentResponseDto> model = EntityModel.of(dto);

        // Link auf den Kommentar selbst
        model.add(linkTo(methodOn(CommentController.class)
                .getComment(comment.getTask().getId(), comment.getId())).withSelfRel());

        // Link auf den Autor
        model.add(linkTo(methodOn(UserController.class)
                .getUserById(comment.getAuthor().getId())).withRel("author"));

        // Link auf den übergeordneten Task
        model.add(linkTo(methodOn(TaskController.class)
                .getTaskById(comment.getTask().getId())).withRel("task"));

        return model;
    }
}
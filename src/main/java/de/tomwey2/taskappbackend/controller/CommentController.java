package de.tomwey2.taskappbackend.controller;

import de.tomwey2.taskappbackend.dto.CommentModelAssembler;
import de.tomwey2.taskappbackend.dto.CommentRequestDto;
import de.tomwey2.taskappbackend.dto.CommentResponseDto;
import de.tomwey2.taskappbackend.model.Comment;
import de.tomwey2.taskappbackend.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments") // Verschachtelte Route
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Operations that affect comments of a task")
public class CommentController {

    private final CommentService commentService;
    private final CommentModelAssembler commentModelAssembler;

    @GetMapping
    @Operation(summary = "Get all comments of a given task")
    public CollectionModel<EntityModel<CommentResponseDto>> getAllComments(
            @Parameter(description = "Id of the task")  // Swagger-UI
            @PathVariable Long taskId) {
        List<Comment> comments = commentService.getCommentsForTask(taskId);

        List<EntityModel<CommentResponseDto>> commentModels = comments.stream()
                .map(commentModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(commentModels,
                linkTo(methodOn(CommentController.class).getAllComments(taskId)).withSelfRel(),
                linkTo(methodOn(TaskController.class).getTaskById(taskId)).withRel("task"));
    }

    @PostMapping
    @Operation(summary = "Create a new comment of a given task")
    public ResponseEntity<EntityModel<CommentResponseDto>> createComment(
            @Parameter(description = "Id of the task")  // Swagger-UI
            @PathVariable Long taskId,
            @Valid @RequestBody CommentRequestDto commentRequestDto) {

        Comment newComment = commentService.createComment(taskId, commentRequestDto);
        EntityModel<CommentResponseDto> entityModel = commentModelAssembler.toModel(newComment);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @GetMapping("/{commentId}")
    @Operation(summary = "Retrieves a comment with a given comment id of a given task")
    public EntityModel<CommentResponseDto> getComment(
            @Parameter(description = "Id of the task")  // Swagger-UI
            @PathVariable Long taskId,
            @Parameter(description = "Id of the comment")  // Swagger-UI
            @PathVariable Long commentId) {

        Comment comment = commentService.getComment(taskId, commentId);
        return commentModelAssembler.toModel(comment);
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "Update the comment with a given comment id of a given task (with check of the autor)")
    public EntityModel<CommentResponseDto> updateComment(
            @Parameter(description = "Id of the task")  // Swagger-UI
            @PathVariable Long taskId,
            @Parameter(description = "Id of the comment")  // Swagger-UI
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequestDto commentRequestDto) {

        Comment updatedComment = commentService.updateComment(taskId, commentId, commentRequestDto);
        return commentModelAssembler.toModel(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete the comment with a given comment id of a given task (with check of the autor)")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "Id of the task")  // Swagger-UI
            @PathVariable Long taskId,
            @Parameter(description = "Id of the comment")  // Swagger-UI
            @PathVariable Long commentId) {

        commentService.deleteComment(taskId, commentId);
        return ResponseEntity.noContent().build();
    }
}
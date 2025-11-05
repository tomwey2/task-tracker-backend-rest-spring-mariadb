package de.tomwey2.taskappbackend.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import de.tomwey2.taskappbackend.dto.UserModelAssembler;
import de.tomwey2.taskappbackend.dto.UserRequestDto;
import de.tomwey2.taskappbackend.dto.UserResponseDto;
import de.tomwey2.taskappbackend.model.User;
import de.tomwey2.taskappbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operations that affect users")
public class UserController {

    private final UserService userService;
    private final UserModelAssembler userModelAssembler;

    @Operation(
        summary = "Create a new user",
        description = "Creates a new user."
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(
                responseCode = "404",
                description = "User with this username already exists."
            ),
        }
    )
    @PostMapping
    public ResponseEntity<EntityModel<UserResponseDto>> createUser(
        @RequestBody UserRequestDto userRequestDto
    ) {
        User user = userService.createUser(userRequestDto);
        return new ResponseEntity<>(
            userModelAssembler.toModel(user),
            HttpStatus.CREATED
        );
    }

    @Operation(
        summary = "Get all users",
        description = "Retrieves a list of all user."
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(
                responseCode = "400",
                description = "Bad Request",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = @Content
            ),
        }
    )
    @GetMapping
    public CollectionModel<EntityModel<UserResponseDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return userModelAssembler
            .toCollectionModel(users)
            .add(
                linkTo(
                    methodOn(UserController.class).getAllUsers()
                ).withSelfRel()
            );
    }

    @Operation(
        summary = "Get a user with a given id",
        description = "Retrieves a user with a given task id."
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(
                responseCode = "404",
                description = "User with id Not Found",
                content = @Content
            ),
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponseDto>> getUserById(
        @PathVariable Long id
    ) {
        Optional<User> user = userService.getUserById(id);
        return user
            .map(userModelAssembler::toModel)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}

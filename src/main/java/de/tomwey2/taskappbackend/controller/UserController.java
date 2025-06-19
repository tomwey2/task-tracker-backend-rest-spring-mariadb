package de.tomwey2.taskappbackend.controller;

import de.tomwey2.taskappbackend.dto.UserModelAssembler;
import de.tomwey2.taskappbackend.dto.UserRequestDto;
import de.tomwey2.taskappbackend.model.User;
import de.tomwey2.taskappbackend.dto.UserResponseDto;
import de.tomwey2.taskappbackend.service.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserModelAssembler userModelAssembler;

    @PostMapping
    public ResponseEntity<EntityModel<UserResponseDto>> createUser(@RequestBody UserRequestDto userRequestDto) {
        User user = userService.createUser(userRequestDto);
        return new ResponseEntity<>(userModelAssembler.toModel(user), HttpStatus.CREATED);
    }

    @GetMapping
    public CollectionModel<EntityModel<UserResponseDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        // Der Assembler bietet auch eine Methode, um eine ganze Collection zu konvertieren.
        // Wir fügen noch den Self-Link für die Collection hinzu.
        return userModelAssembler.toCollectionModel(users)
                .add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponseDto>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(userModelAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
package de.tomwey2.taskappbackend.dto;

import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

// Ein DTO, das die öffentlichen Informationen eines Benutzers repräsentiert.
// Kein Passwort hier!
@Relation(collectionRelation = "users", itemRelation = "user")
public record UserResponseDto(
        Long id,
        String username,
        String email,
        String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
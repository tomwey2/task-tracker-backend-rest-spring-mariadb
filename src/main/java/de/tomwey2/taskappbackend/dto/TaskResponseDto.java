package de.tomwey2.taskappbackend.dto;

import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Ein DTO, das einen Task repräsentiert.
// Statt der vollen User-Entität enthält es nur das UserDto.
@Relation(collectionRelation = "tasks", itemRelation = "task")
public record TaskResponseDto(
        Long id,
        String title,
        String description,
        String comment,
        String state,
        LocalDate deadline,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
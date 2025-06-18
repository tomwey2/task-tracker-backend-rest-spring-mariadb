package de.tomwey2.taskappbackend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Ein DTO, das einen Task repräsentiert.
// Statt der vollen User-Entität enthält es nur das UserDto.
public record TaskResponseDto(
        Long id,
        String title,
        String description,
        String state,
        LocalDate deadline,
        UserResponseDto reportedBy, // Hier verwenden wir unser UserDto
        UserResponseDto assignedTo,
        ProjectResponseDto belongsTo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
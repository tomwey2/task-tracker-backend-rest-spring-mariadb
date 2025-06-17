package de.tomwey2.taskappbackend.dto;

import java.time.LocalDateTime;

// Ein DTO, das einen Task repräsentiert.
// Statt der vollen User-Entität enthält es nur das UserDto.
public record TaskResponseDto(
        Long id,
        String title,
        String description,
        String state,
        UserResponseDto reportedBy, // Hier verwenden wir unser UserDto
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
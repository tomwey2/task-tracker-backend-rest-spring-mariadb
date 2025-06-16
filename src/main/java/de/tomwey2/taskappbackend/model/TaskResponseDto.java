package de.tomwey2.taskappbackend.model;

import java.time.LocalDateTime;

// Ein DTO, das einen Task repräsentiert.
// Statt der vollen User-Entität enthält es nur das UserDto.
public record TaskResponseDto(
        Long id,
        String title,
        String description,
        boolean completed,
        LocalDateTime createdAt,
        UserResponseDto reportedBy // Hier verwenden wir unser UserDto
) {}
package de.tomwey2.taskappbackend.dto;

import java.time.LocalDateTime;

// Ein DTO, das die öffentlichen Informationen eines Benutzers repräsentiert.
// Kein Passwort hier!
public record UserResponseDto(
        Long id,
        String username,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
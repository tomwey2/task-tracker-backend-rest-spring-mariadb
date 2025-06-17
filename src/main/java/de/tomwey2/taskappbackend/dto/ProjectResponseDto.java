package de.tomwey2.taskappbackend.dto;

import java.time.LocalDateTime;

public record ProjectResponseDto(
        Long id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
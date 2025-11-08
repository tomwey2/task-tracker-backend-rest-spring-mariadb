package de.tomwey2.taskappbackend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO für die Erstellung oder Aktualisierung eines Kommentars.
 */
public record CommentRequestDto(
        @NotBlank(message = "Content cannot be blank")
        String content
) {
}


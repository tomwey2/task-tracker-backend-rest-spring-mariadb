package de.tomwey2.taskappbackend.dto;

import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

/**
 * DTO für die Darstellung eines Kommentars in API-Antworten.
 */
@Relation(collectionRelation = "comments", itemRelation = "comment")
public record CommentResponseDto(
        Long id,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
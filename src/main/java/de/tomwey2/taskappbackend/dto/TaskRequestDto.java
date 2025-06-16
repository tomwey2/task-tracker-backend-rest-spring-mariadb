package de.tomwey2.taskappbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Daten, die vom Client kommen, um einen neuen Task zu erstellen.
public record TaskRequestDto(

        @NotBlank(message = "Title cannot be blank")
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,

        @Size(max = 1000, message = "Description can be max 1000 characters")
        String description
) {}
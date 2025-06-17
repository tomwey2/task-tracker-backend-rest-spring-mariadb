package de.tomwey2.taskappbackend.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

// Daten, die vom Client kommen, um einen neuen Task zu erstellen.
public record TaskRequestDto(

        @NotBlank(message = "Title cannot be blank")
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,

        @Size(max = 1000, message = "Description can be max 1000 characters")
        String description,

        @NotNull(message = "State is required")
        String state,

        @NotNull(message = "Due date is required")
        @FutureOrPresent(message = "Due date must be in the present or future")
        LocalDate dueDate
) {}
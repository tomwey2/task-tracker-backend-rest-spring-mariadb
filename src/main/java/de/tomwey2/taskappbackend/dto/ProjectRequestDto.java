package de.tomwey2.taskappbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequestDto (
    @NotBlank(message = "Project name cannot be blank")
    @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters")
    String name
) {}

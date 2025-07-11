package de.tomwey2.taskappbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDto(
        @NotBlank(message = "User name cannot be blank")
        @Size(min = 3, max = 100, message = "User name must be between 3 and 100 characters")
        String username,

        @NotBlank(message = "Email cannot be blank")
        @Size(min = 3, max = 100, message = "Email must be between 3 and 100 characters")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 3, max = 100, message = "Password must be between 3 and 100 characters")
        String password,

        @NotBlank(message = "Role cannot be blank")
        String role

) {
}

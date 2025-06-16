package de.tomwey2.taskappbackend.model;

// Ein DTO, das die öffentlichen Informationen eines Benutzers repräsentiert.
// Kein Passwort hier!
public record UserDto(
        Long id,
        String username
) {}
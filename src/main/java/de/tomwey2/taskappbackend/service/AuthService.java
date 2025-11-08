package de.tomwey2.taskappbackend.service;

import de.tomwey2.taskappbackend.exception.ResourceNotFoundException;
import de.tomwey2.taskappbackend.model.User;
import de.tomwey2.taskappbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    /**
     * Ruft den aktuell authentifizierten Benutzer aus dem SecurityContext ab.
     *
     * @return Die User-Entität des eingeloggten Benutzers.
     * @throws ResourceNotFoundException   wenn kein Benutzerkontext gefunden wird oder der Benutzer nicht in der DB existiert.
     */
    public User getCurrentUser() {
        // 1. Hole das Authentication-Objekt aus dem SecurityContextHolder.
        //    Dies wurde von unserem JwtAuthenticationFilter dort platziert.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("No authenticated user found in security context.");
        }

        // 2. Der "Name" des Authentication-Objekts ist der Username, den wir im JWT gespeichert haben.
        String username = authentication.getName();

        // 3. Lade den Benutzer aus der Datenbank, um das vollständige, aktuelle Entitätsobjekt zu erhalten.
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found in database: " + username));
    }
}